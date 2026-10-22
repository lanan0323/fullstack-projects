package com.milktea.user.mapper;

import com.milktea.model.entity.Employee;
import com.milktea.model.vo.EmployeeVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("<script>" +
            "SELECT e.id, e.user_id, e.employee_no, e.real_name, e.gender, e.position, e.status, e.create_time, " +
            "u.phone, r.role_name, r.id AS role_id " +
            "FROM employee e " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "<where> " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (e.real_name LIKE CONCAT('%', #{keyword}, '%') OR e.employee_no LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</where> " +
            "ORDER BY e.create_time DESC" +
            "</script>")
    List<EmployeeVO> page(@Param("keyword") String keyword);

    @Select("SELECT e.id, e.user_id, e.employee_no, e.real_name, e.gender, e.position, e.status, e.create_time, " +
            "u.phone, r.role_name, r.id AS role_id " +
            "FROM employee e " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "WHERE e.id = #{id}")
    EmployeeVO findById(Long id);

    @Insert("INSERT INTO employee(user_id, employee_no, real_name, gender, position, status) " +
            "VALUES(#{userId}, #{employeeNo}, #{realName}, #{gender}, #{position}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Employee employee);

    @Update("UPDATE employee SET employee_no = #{employeeNo}, real_name = #{realName}, gender = #{gender}, position = #{position} WHERE id = #{id}")
    int update(Employee employee);

    @Delete("DELETE FROM employee WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE employee SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT user_id FROM employee WHERE id = #{id}")
    Long findUserIdById(Long id);
}