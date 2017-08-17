package com.abstratt.kirra.rest.resources;

import java.util.List;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement.Page;

class InstanceList extends Page<Instance> {
    public InstanceList(List<Instance> contents) {
        super(contents);
    }
    public InstanceList(List<Instance> contents, long offset, long length) {
        super(contents, offset, length);
    }
}