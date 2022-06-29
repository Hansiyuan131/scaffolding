package com.yuanstack.sca.service.system.assembly.third.km;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @description: KmAuthDTO
 * @author: hansiyuan
 * @date: 2022/6/29 3:39 PM
 */
@Getter
@Setter
public class KmAuthDTO {

    private String  url;

    private Map<String, String> heardMap ;

    private String httpMethod;

    private String reqBody;

}
