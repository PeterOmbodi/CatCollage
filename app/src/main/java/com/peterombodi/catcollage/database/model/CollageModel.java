package com.peterombodi.catcollage.database.model;

import com.peterombodi.catcollage.database.CatCollageDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Peter on 25.09.2017.
 */

@Table(database = CatCollageDatabase.class)
public class CollageModel extends BaseModel {

	public CollageModel() {
		this.id = UUID.randomUUID();
	}

	public CollageModel(String name, List<CollageItem> items) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.items = items;
		this.createDT = new GregorianCalendar().getTime().getTime();
		for (CollageItem collageItem: items){
			collageItem.collageId = this.id;
		}
	}

	@PrimaryKey // at least one primary key required
	UUID id;

	@Column
	String name;

	@Column
	long createDT;

	List<CollageItem> items;

	@OneToMany(methods = {OneToMany.Method.ALL}, variableName = "items")
	public List<CollageItem> getItems() {
		if (items == null || items.isEmpty()) {
			items = SQLite.select()
				.from(CollageItem.class)
				.where(CollageItem_Table.collageId.eq(id))
				.queryList();
		}
		return items;
	}


}
