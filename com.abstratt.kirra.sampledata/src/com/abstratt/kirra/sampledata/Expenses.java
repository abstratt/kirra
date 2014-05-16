package com.abstratt.kirra.sampledata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Namespace;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;

public class Expenses implements SampleBuilder {
	@Override
	public Schema getSchema() {
		Schema newSchema = new Schema();
		List<Namespace> namespaces = new ArrayList<Namespace>();
		Namespace expenseNamespace = new Namespace("expenses");
		namespaces.add(expenseNamespace);
		List<Entity> expenseEntities = new ArrayList<Entity>();
		Entity categoryEntity = buildExpenseEntity();
		expenseEntities.add(categoryEntity);
		expenseNamespace.setEntities(expenseEntities);

		newSchema.setNamespaces(namespaces);
		return newSchema;
	}

	@Override
	public List<Instance> getInstances(String namespace, String name) {
		List<Instance> expenses = new ArrayList<Instance>();
		try {
			buildExpenses(expenses);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return expenses;
	}

	private void buildExpenses(List<Instance> expenses) throws ParseException {
		TypeRef expenseType = new TypeRef("expenses", "Expense", TypeKind.Entity);
		Instance expense1 = new Instance(expenseType, "1");
		expense1.setValue("amount", 230.56);
		expense1.setValue("description", "Hilton LA downtown");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		expense1.setValue("submissionDate", sdf.parse("2014/05/15"));
		expense1.setValue("expenseDate", sdf.parse("2014/05/01"));
		expenses.add(expense1);
		
		Instance expense2 = new Instance(expenseType, "2");
		expense2.setValue("amount", 45.20);
		expense2.setValue("description", "Cab airport->downtown");
		expense2.setValue("submissionDate", sdf.parse("2014/05/15"));
		expense2.setValue("expenseDate", sdf.parse("2014/05/03"));
		expenses.add(expense2);
	}

	private Entity buildExpenseEntity() {
		Entity expenseEntity = new Entity();
		expenseEntity.setName("expense");
		expenseEntity.setLabel("Expense");
		List<Property> properties = new ArrayList<Property>();
		expenseEntity.setProperties(properties);
		properties.add(buildPrimitiveProperty("description", "Description", "String"));
		properties.add(buildPrimitiveProperty("expenseDate", "expenseDate", "Date"));
		properties.add(buildPrimitiveProperty("submissionDate", "submissionDate", "Date"));
		properties.add(buildPrimitiveProperty("amount", "amount", "Double"));
		return expenseEntity;
	}

	private Property buildPrimitiveProperty(String name, String label, String type) {
		Property description = new Property();
		description.setName(name);
		description.setLabel(label);
		TypeRef descriptionType = new TypeRef(type, TypeKind.Primitive);
		description.setTypeRef(descriptionType);
		return description;
	}
	
	public static void main(String[] args) {
		Expenses expensesApp = new Expenses();
		Schema schema = expensesApp.getSchema();
		List<Instance> expenses = expensesApp.getInstances("expenses" , "Expense");
		System.out.println(schema);
	}
}
