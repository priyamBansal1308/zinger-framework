package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.CollegeColumn;
import com.food.ordering.zinger.column.logger.CollegeLogColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.ResponseHeaderModel;
import com.food.ordering.zinger.model.logger.CollegeLogModel;
import com.food.ordering.zinger.query.CollegeQuery;
import com.food.ordering.zinger.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CollegeDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<String> insertCollege(CollegeModel collegeModel, ResponseHeaderModel responseHeader) {
        Response<String> response = new Response<>();
        CollegeLogModel collegeLogModel = new CollegeLogModel();

        try {

            if (!responseHeader.getRole().equals((UserRole.SUPER_ADMIN).name())) {
                response.setCode(ErrorLog.InvalidHeader1000);
                response.setMessage(ErrorLog.InvalidHeader);
            }
            else if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.InvalidHeader1001);
                response.setMessage(ErrorLog.InvalidHeader);
            }
            else{
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(CollegeColumn.name, collegeModel.getName())
                        .addValue(CollegeColumn.address, collegeModel.getAddress())
                        .addValue(CollegeColumn.iconUrl, collegeModel.getIconUrl());

                int responseValue = namedParameterJdbcTemplate.update(CollegeQuery.insertCollege, parameters);
                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;
    }

    public Response<List<CollegeModel>> getAllColleges(ResponseHeaderModel responseHeader) {
        Response<List<CollegeModel>> response = new Response<>();
        List<CollegeModel> list = null;
        CollegeLogModel collegeLogModel = new CollegeLogModel();
        collegeLogModel.setId(collegeLogModel.getId());
        collegeLogModel.setMobile(responseHeader.getMobile());

        collegeLogModel.setErrorCode(response.getCode());
        collegeLogModel.setMessage(response.getMessage());
        collegeLogModel.setUpdatedValue(responseHeader.getMobile());

        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.InvalidHeader1002);
                response.setMessage(ErrorLog.InvalidHeader);

                collegeLogModel.setErrorCode(response.getCode());
                collegeLogModel.setMessage(response.getMessage());
                collegeLogModel.setPriority(Priority.HIGH);
                collegeLogModel.setUpdatedValue(responseHeader.getMobile());

                try {
                    auditLogDao.insertCollegeLog(collegeLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            try {
                list = namedParameterJdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
        try {
            auditLogDao.insertCollegeLog(collegeLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<CollegeModel> getCollegeById(Integer collegeId) {
        Response<CollegeModel> response = new Response<>();
        CollegeModel college = null;
        CollegeLogModel collegeLogModel = new CollegeLogModel();

        collegeLogModel.setId(collegeLogModel.getId());
        collegeLogModel.setErrorCode(response.getCode());
        collegeLogModel.setMessage(response.getMessage());
        collegeLogModel.setUpdatedValue(collegeId.toString());

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeColumn.id, collegeId);

            try {
                college = namedParameterJdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (college != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(college);

                collegeLogModel.setErrorCode(response.getCode());
                collegeLogModel.setMessage(response.getMessage());
                collegeLogModel.setPriority(Priority.LOW);
                collegeLogModel.setUpdatedValue(collegeId.toString());
            }
        }

        try {
            auditLogDao.insertCollegeLog(collegeLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
