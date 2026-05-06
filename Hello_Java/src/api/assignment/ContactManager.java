package api.assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class ContactManager {
    private HashMap<String, String> contacts;

    public ContactManager() {
        this.contacts = new HashMap<>();
    }

    public boolean add(String name, String phone) {
        if (name == null || phone == null || contacts.containsKey(name)) {
            return false;
        }
        contacts.put(name, phone);
        return true;
    }

    public String find(String name) {
        return contacts.get(name);
    }

    public boolean delete(String name) {
        if (!contacts.containsKey(name)) {
            return false;
        }
        contacts.remove(name);
        return true;
    }

    public ArrayList<String> show() {
        ArrayList<String> sortedNames = new ArrayList<>(contacts.keySet());
        Collections.sort(sortedNames);

        ArrayList<String> result = new ArrayList<>();
        for (String name : sortedNames) {
            result.add(name + " " + contacts.get(name));
        }
        return result;
    }
    
}