package com.abstratt.kirra.rest.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.abstratt.kirra.Blob;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.KirraContext.Upload;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCE_BLOB_PATH)
public class InstanceBlobResource {
	private static final int ATTACHMENT_LIMIT = 512 * 1024;

	@DELETE
	@Path("{blobToken}")
	@Produces(MediaType.APPLICATION_JSON)
    public void removeBlob(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId,
    		@PathParam("propertyName") String blobPropertyName,@PathParam("blobToken") String blobToken) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkInstanceUpdateAuthorized(entityRef, objectId);
		InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
		Instance instance = instanceManagement.getInstance(entityRef.getEntityNamespace(),
				entityRef.getTypeName(), objectId, true);
		ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
		Blob blob = (Blob) instance.getValue(blobPropertyName);
		ResourceHelper.ensure(blob != null, "Blob not found", Status.NOT_FOUND);
		instanceManagement.deleteBlob(entityRef, objectId, blobPropertyName, blobToken);
    }
	
	@GET
	@Path("{blobToken}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadBlob(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId,
    		@PathParam("propertyName") String blobPropertyName,@PathParam("blobToken") String blobToken) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkInstanceReadAuthorized(entityRef, objectId);
		InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
		Instance instance = instanceManagement.getInstance(entityRef.getEntityNamespace(),
				entityRef.getTypeName(), objectId, true);
		ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
		Blob blob = instanceManagement.getBlob(entityRef, objectId, blobPropertyName, blobToken);
		ResourceHelper.ensure(blob != null, "Blob not found", Status.NOT_FOUND);
		ByteArrayInputStream blobContents = new ByteArrayInputStream(blob.getContents());
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream paramOutputStream) throws IOException, WebApplicationException {
				IOUtils.copy(blobContents, paramOutputStream);
				paramOutputStream.flush();
			}
		};
		return Response.ok(stream).build();
    }

	
	
	@POST
	@Produces("application/json")
	@Consumes("*/*")
	public void addBlob(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId,
			@PathParam("propertyName") String blobPropertyName) {
		TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
		Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
		ResourceHelper.ensure(entity != null, "Entity not found", Status.NOT_FOUND);
		AuthorizationHelper.checkInstanceUpdateAuthorized(entityRef, objectId);
		Property blobProperty = entity.getProperty(blobPropertyName);
		ResourceHelper.ensure(blobProperty != null, "Property not found", Status.NOT_FOUND);
		ResourceHelper.ensure(TypeKind.Blob == blobProperty.getTypeRef().getKind(), "Not a blob type",
				Status.BAD_REQUEST);
		List<Upload> uploads = KirraContext.getUploads();
		ResourceHelper.ensure(!uploads.isEmpty(), "No attachment found", Status.BAD_REQUEST);
		ResourceHelper.ensure(uploads.size() == 1 || blobProperty.isMultiple(), "Too many attachments received",
				Status.BAD_REQUEST);

		uploads.forEach(it -> ResourceHelper.ensure(it.getContents().length() <= ATTACHMENT_LIMIT,
				"Attachments limited to " + (ATTACHMENT_LIMIT/1024) + " kb, actual size: " + (it.getContents().length()/1024) + " kb", Status.BAD_REQUEST));
		ResourceHelper.ensure(uploads.size() == 1 || blobProperty.isMultiple(), "Too many attachments received",
				Status.BAD_REQUEST);
		
		byte[] blobContents;
		try {
			blobContents = FileUtils.readFileToByteArray(uploads.get(0).getContents());
			Blob newBlob = new Blob(UUID.randomUUID().toString(), blobContents.length, blobContents, uploads.get(0).getContentType(), uploads.get(0).getOriginalName());
			KirraContext.getInstanceManagement().addBlob(entityRef, objectId, blobPropertyName, newBlob);
		} catch (IOException e) {
			ResourceHelper.fail(e, "Error processing attachment", Status.INTERNAL_SERVER_ERROR);
		}
	}
}
