package caretechapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import caretechapp.dbutil.DBConnection;
import caretechapp.pojo.EmpPojo;
import caretechapp.pojo.DoctorPojo;
import caretechapp.pojo.ReceptionistPojo;

public class EmpDao {
public static String getNextEmpId()throws SQLException{
    Connection conn=DBConnection.getConnection();
    Statement st=conn.createStatement();
    ResultSet rs=st.executeQuery("Select max(emp_id) from employees");
    rs.next();
    String str=rs.getString(1);
    int empId=101;
    if(str!=null){
        String id=str.substring(1);
        empId=Integer.parseInt(id);
        empId++;
    }
    String newId="E"+empId;
    return newId;
}
public static boolean addEmp(EmpPojo emp)throws SQLException{
    Connection conn=DBConnection.getConnection();
    PreparedStatement ps=conn.prepareStatement("Insert into employees values(?,?,?,?)");
    ps.setString(1, emp.getEmpId());
    ps.setString(2, emp.getEmpName());
    ps.setString(3, emp.getEmpDept());
    ps.setDouble(4,emp.getEmpSal());
    return ps.executeUpdate()==1;
}
public static List<String> getAllemployeeId()throws SQLException{
    Connection conn=DBConnection.getConnection();
    Statement st=conn.createStatement();
    ResultSet rs=st.executeQuery("Select emp_id from employees");
    List<String> empIdList=new ArrayList<>();
    while(rs.next()){
        empIdList.add(rs.getString(1));
    }
    return empIdList;
}       
 public static EmpPojo getEmployeeDetails(String empId)throws SQLException{
    Connection conn=DBConnection.getConnection();
    PreparedStatement ps=conn.prepareStatement("Select * from employees where emp_id=?");
    ps.setString(1,empId);
    ResultSet rs=ps.executeQuery();
    rs.next();
    EmpPojo emp=new EmpPojo();
    emp.setEmpId(rs.getString(1));
    emp.setEmpName(rs.getString(2));
    emp.setEmpDept(rs.getString(3));
    emp.setEmpSal(rs.getDouble(4));
    return emp;
    
 } 
 public static boolean updateEmployee(EmpPojo emp)throws SQLException{
     updateName(emp);
     Connection conn=DBConnection.getConnection();
     PreparedStatement ps=conn.prepareStatement("Update employees set emp_name=?,emp_salary=? where emp_id=?");
     ps.setString(1, emp.getEmpName());
     ps.setDouble(2, emp.getEmpSal());
     ps.setString(3, emp.getEmpId());
     return ps.executeUpdate()==1;
 }
 public static void updateName(EmpPojo emp)throws SQLException{
     Connection conn=DBConnection.getConnection();
     PreparedStatement ps=conn.prepareStatement("Select emp_name from employees where emp_id=?");
     ps.setString(1, emp.getEmpId());
     ResultSet rs=ps.executeQuery();
     rs.next();
     String currName=rs.getString(1);
     String newName=emp.getEmpName();
     UserDao.updateName(currName,newName);
     if(emp.getEmpDept().equalsIgnoreCase("Receptionist"))
         ReceptionistDao.updateName(currName,newName);
     else if(emp.getEmpDept().equalsIgnoreCase("Doctor"))
         DoctorsDao.updateName(currName,newName);
     
 }
 public static List<EmpPojo> getAllEmployeeDetails()throws SQLException{
    Connection conn=DBConnection.getConnection();
    Statement st=conn.createStatement();
    ResultSet rs=st.executeQuery("Select * from employees order by emp_id");
    List<EmpPojo>empList=new ArrayList<>();
    while(rs.next()){
    EmpPojo emp=new EmpPojo();
    emp.setEmpId(rs.getString(1));
    emp.setEmpName(rs.getString(2));
    emp.setEmpDept(rs.getString(3));
    emp.setEmpSal(rs.getDouble(4));
    empList.add(emp);
    }
    return empList;
    
 } 
  public static Map<String,String> getUnRegisteredDoctors()throws SQLException{
   Connection conn=DBConnection.getConnection();
   Statement st=conn.createStatement();
   ResultSet rs=st.executeQuery("Select EMP_ID,EMP_NAME from EMPLOYEES where EMP_DEPARTMENT='DOCTOR' and EMP_NAME not in (select USER_NAME from USERS where USER='DOCTOR')order by Emp_ID");
   Map<String,String> unRegDocList=new HashMap<>();
   while(rs.next()){
       String id=rs.getString(1);
       String name=rs.getString(2);
       unRegDocList.put(id, name);
   }
   return unRegDocList;
 }

   public static Map<String,String> getUnRegisteredReceptionists()throws SQLException{
   Connection conn=DBConnection.getConnection();
   Statement st=conn.createStatement();
   ResultSet rs=st.executeQuery("Select emp_id,emp_name from employees where emp_department='RECEPTIONIST' and emp_name not in (select user_name from users where user='RECEPTIONIST')order by emp_id");
   Map<String,String> unRegRecepList=new HashMap<>();
   while(rs.next()){
       String id=rs.getString(1);
       String name=rs.getString(2);
       unRegRecepList.put(id, name);
   }
   return unRegRecepList;
 }
   public static boolean deleteEmployeeById(String empId)throws SQLException{
     Connection conn=DBConnection.getConnection();
     PreparedStatement ps=conn.prepareStatement("Select emp_name from employees where emp_id=?");
     ps.setString(1, empId);
     ResultSet rs=ps.executeQuery();
     rs.next();
     String empName=rs.getString(1);
     UserDao.deleteUserByName(empName);
     DoctorsDao.deleteDoctorByName(empName);
     ReceptionistDao.deleteReceptionistByName(empName);
     ps=conn.prepareStatement("Delete from employees where emp_id=?");
     ps.setString(1, empId);
     return ps.executeUpdate()==1;
}
   public static void updateName(String currName,String newName)throws SQLException{
     Connection conn=DBConnection.getConnection();
     PreparedStatement ps=conn.prepareStatement("Update employees set emp_name=? where emp_name=?");
     ps.setString(1, newName);
     ps.setString(2, currName);
}
   
}
