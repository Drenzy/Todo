// Update the Item class
package com.example.todolist;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {

    // Fields representing the properties of an item
    String Todo;            // Task name
    String ItemDescription; // Task description
    String Type;            // Task type
    Date CreatedDate;       // Task creation date
    Date TodoTime;          // Task completion time
    int IconResourceId;     // Resource ID for the task icon
    boolean isChecked;      // Checkbox state for task completion

    // Getter for the checkbox state
    public boolean isChecked() {
        return isChecked;
    }

    // Setter for the checkbox state
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
