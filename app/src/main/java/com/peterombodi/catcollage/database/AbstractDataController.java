package com.peterombodi.catcollage.database;

import com.peterombodi.catcollage.database.model.CollageModel;

/**
 * Created by Peter on 22.09.2017.
 */

public interface AbstractDataController {

	CollageModel getCollage(long id);

}
