package com.abernathy.webinterface.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.abernathy.webinterface.bean.ReportBean;

@FeignClient(name = "microservice-reports", url = "${reports.url}")
public interface MicroserviceReportsProxy {

	@GetMapping("api/reports/generate/{patientId}")
	ReportBean generateReport(@PathVariable String patientId);
}
