// Update the Item class
package com.example.todolist;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
    String Todo;
    String ItemDescription;
    String Type;
    Date CreatedDate;
    Date TodoTime;
    int IconResourceId;
    boolean isChecked; // Add a field for checkbox state

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
