package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class ManipProducts {

    private Connection myConnection;

    ManipProducts(Connection connection)
    {  
        this.myConnection = connection;
    }

    public void addProduct(String ref, String des, ArrayList<Operation> operations) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(Statement s = this.myConnection.createStatement()){

            ResultSet res = s.executeQuery("SELECT COUNT(*) FROM OPERATIONS");
            res.next();

            int lastID = res.getInt(1);

            try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                "INSERT INTO PRODUCT "
                + "(REF, DES) "
                + "VALUES(?, ?);"
            ))
            {
                pStatement.setString(1, ref);
                pStatement.setString(2, des);
                pStatement.executeUpdate();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                this.myConnection.rollback();
            }
            for(int i = 0; i < operations.size(); i++)
            {
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "INSERT INTO OPERATIONS "
                    +   "(IDTYPE, IDPRODUCT) "
                    +    "VALUES(?, ?);"
                ))
                {
                    pStatement.setInt(1, operations.get(i).idtype);
                    pStatement.setInt(2, operations.get(i).idproduct);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
            }
            this.myConnection.commit();
            for(int i = 0; i < operations.size(); i++)
            {
                
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "UPDATE OPERATIONS "
                    +   "SET OPERATIONS.OPBEF = ? WHERE OPERATIONS.ID = ?;"
                ))
                {
                    if(operations.get(i).opbef != 0)
                    {
                        pStatement.setInt(1, operations.get(i).opbef + lastID);
                    }
                    else
                    {
                        pStatement.setNull(1, Types.INTEGER);
                    }

                    pStatement.setInt(2, lastID + i + 1);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
            }
            for(int i = 0; i < operations.size(); i++)
            {
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "UPDATE OPERATIONS "
                    +   "SET OPERATIONS.OPAFT = ? WHERE OPERATIONS.ID = ?;"
                ))
                {
                    if(operations.get(i).opaft != 0)
                    {
                        pStatement.setInt(1, operations.get(i).opaft + lastID);
                    }
                    else
                    {
                        pStatement.setNull(1, Types.INTEGER);
                    }
                    pStatement.setInt(2, lastID + i + 1);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
        }

        }

        this.myConnection.commit();

        this.myConnection.setAutoCommit(true);
    }

    public void loadDefaultProducts() throws SQLException
    {
        ArrayList<Operation> opsBolt = new ArrayList<>();
        opsBolt.add(new Operation(3, 1, 0, 2));
        opsBolt.add(new Operation(5, 1, 1, 3));
        opsBolt.add(new Operation(1, 1, 2, 0));
        addProduct("FFFFFE", "Bolt", opsBolt);

        ArrayList<Operation> opsSprocket = new ArrayList<>();
        opsSprocket.add(new Operation(3, 2, 0, 2));
        opsSprocket.add(new Operation(13, 2, 1, 0));
        addProduct("FFFFFD", "Sprocket", opsSprocket);
    }
    
}