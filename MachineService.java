package com.project.views.production;

import java.sql.SQLException;
import java.util.List;
import com.project.ManipMachines;
import com.project.ManipMachines.MachineStateTimetable;

public interface MachineService 
{

    List<ManipMachines.MachineStateTimetable> getMachineStateHistory(String reference) throws SQLException;

    List<MachineStateTimetable> getAllMachineStateTimetables();
}



