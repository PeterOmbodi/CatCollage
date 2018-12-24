package com.peterombodi.catcollage.database.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by Peter on 25.09.2017.
 */

public class CollageModel {

	UUID id;

	String name;

	long createDT;

	List<CollageItem> items;

}
