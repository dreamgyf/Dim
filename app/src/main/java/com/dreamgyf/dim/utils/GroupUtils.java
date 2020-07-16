package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.entity.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupUtils {

	private final static List<Group> groupList = Collections.synchronizedList(new ArrayList<>());

	public static Group getGroup(int position) {
		return groupList.get(position);
	}

	public static int groupCount() {
		return groupList.size();
	}

	public static Group findGroup(int id) {
		for(int i = groupList.size() - 1; i >= 0; i--) {
			if(groupList.get(i).getId() == id) {
				return groupList.get(i);
			}
		}
		return null;
	}

	public static void addGroup(Group group) {
		if(groupList.isEmpty()) {
			groupList.add(group);
			return;
		}
		for(int i = 0;i < groupList.size();i++) {
			if(group.compareTo(groupList.get(i)) < 0) {
				groupList.add(i, group);
				return;
			}
			if(i == groupList.size() - 1) {
				groupList.add(group);
				return;
			}
		}
	}

	public static void addGroup(List<Group> groups) {
		for(Group group : groups) {
			addGroup(group);
		}
	}

	public static void removeGroup(int id) {
		for(int i = groupList.size() - 1;i >= 0;i--) {
			if(groupList.get(i).getId() == id) {
				groupList.remove(i);
				return;
			}
		}
	}
}
