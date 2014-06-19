package com.abstratt.kirra;

import java.util.List;

public interface BehaviorScope extends NameScope {
    Operation getOperation(String name);

    List<Operation> getOperations();
}
