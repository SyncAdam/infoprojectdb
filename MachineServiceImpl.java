package com.project.views.production;

import java.sql.SQLException;
import java.util.List;
import com.project.ManipMachines;
import com.project.ManipMachines.MachineStateTimetable;

public class MachineServiceImpl implements MachineService {

    private final ManipMachines manipMachines;

    public MachineServiceImpl(ManipMachines manipMachines) 
    {
        this.manipMachines = manipMachines;
    }

    @Override
    public List<ManipMachines.MachineStateTimetable> getMachineStateHistory(String reference) throws SQLException
    {
        return manipMachines.getMachineStateHistory(reference);
    }

    @Override
    public List<MachineStateTimetable> getAllMachineStateTimetables()
     {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllMachineStateTimetables'");
    }
}


