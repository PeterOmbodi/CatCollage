package com.peterombodi.catcollage.database;

import com.peterombodi.catcollage.database.model.CollageModel;

/**
 * Created by Peter on 25.09.2017.
 */

public class DatabaseController implements AbstractDataController {

	private DatabaseController() {
	}

	@Override
	public CollageModel getCollage(long id) {
		return null;
	}

	private static class SingletonHelper {
		private static final DatabaseController INSTANCE = new DatabaseController();
	}

	public static DatabaseController getInstance() {
		return SingletonHelper.INSTANCE;
	}

}
