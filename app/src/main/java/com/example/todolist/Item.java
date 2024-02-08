// Update the Item class
package com.example.todolist;

import android.graphics.drawable.Icon;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
    String Todo;
    String ItemDescription;
    String Type;
    Date CreatedDate;
    Date TodoTime;
    int IconResourceId; // Add a field for icon resource ID
}
