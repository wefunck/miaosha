package com.miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        ValidationResult result = new ValidationResult();
        final Set<ConstraintViolation<Object>> constraintViolationsSet = validator.validate(bean);
        if(constraintViolationsSet.size() > 0){
            //说明有错误
            result.setHasErrors(true);
            constraintViolationsSet.forEach(constraintViolations->{
                String errMsg = constraintViolations.getMessage();
                String propertyName = constraintViolations.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return result;
    }


    //在Spring的bean初始化完之后，会回调以下的方法
    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }
}
