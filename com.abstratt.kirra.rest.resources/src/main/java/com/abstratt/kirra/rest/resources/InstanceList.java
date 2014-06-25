package com.abstratt.kirra.rest.resources;

import java.util.List;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.rest.common.Page;

class InstanceList extends Page<Instance> {
    public InstanceList(List<Instance> contents) {
        super(contents);
    }
}