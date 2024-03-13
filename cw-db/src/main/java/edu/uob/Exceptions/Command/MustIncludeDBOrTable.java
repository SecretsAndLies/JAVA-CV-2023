package edu.uob.Exceptions.Command;

import edu.uob.Exceptions.GenericException;

public class MustIncludeDBOrTable extends GenericException {
    public MustIncludeDBOrTable(){
        super("Your command must include DATABASE or TABLE eg: CREATE TABLE t;.");
    }
}
