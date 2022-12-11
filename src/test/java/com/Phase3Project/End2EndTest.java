package com.Phase3Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class End2EndTest {
	Response response;
	RequestSpecification request ;
	Map<String,Object> MapObj;
	String Ename = RandomStringUtils.randomAlphabetic(10);
	int salary = 4000;
	String baseURI = "http://localhost:3000" ;
	public static int EmpId;
	JsonPath jpath ;
	
	@Test
	public void TestEmp() {
		
		// Base URL
//		RestAssured.baseURI = "http://localhost:3000";
//		request = RestAssured.given();
		// Call Get All Employee
		response = GetAllEmployees() ;
		Assert.assertEquals(200, response.getStatusCode());
		
		//Create new Employee
		response = CreateEmployee(Ename, salary);
		Assert.assertEquals(201, response.getStatusCode());
//		System.out.println(response.getBody().asString());
		
		// Extract new created Employee ID
//		JsonPath jpath = response.jsonPath();
//		EmpId = jpath.get("id");
		System.out.println(EmpId);
		
		// Get Details for created Employee  
		response = GetSingleEmployee(EmpId) ;
		String ResponseBody = response.getBody().asString();
		Assert.assertTrue(ResponseBody.contains(Ename));
		
		// Update Created Employee
		response=UpdateEmployee(EmpId,"Smith", salary);
		Assert.assertEquals(200, response.getStatusCode());
		
		// Check created Employee name Smith
		

		response = GetSingleEmployee(EmpId) ;
		ResponseBody = response.getBody().asString();
		Assert.assertTrue(ResponseBody.contains("Smith"));

		
		// Delete Created Employee
		
		response=DeleteEmployee(EmpId);
		Assert.assertEquals(200, response.getStatusCode());
		
		//Check deleted Employee in Employee List
				response = GetAllEmployees();
				ResponseBody = response.getBody().asString();		
				jpath = response.jsonPath();
				List<String> names = jpath.get("name");
				for(int i=0;i<names.size();i++) {
					Assert.assertNotEquals(names.get(i), "Smith");
				}
				response = GetSingleEmployee(EmpId) ;
				int statusCode= response.getStatusCode();
				
				Assert.assertEquals(404,statusCode);
				
	}

	public Response GetAllEmployees() {
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		
		response = request.get("employees");

		return response;
	}


	public Response GetSingleEmployee(int EmpID) {
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		
		response = request.param("id", EmpID).get("employees/"+EmpID);

		return response;
	}

	public Response CreateEmployee(String EmpName , int EmpSalary) {
		RestAssured.baseURI = this.baseURI;
		MapObj = new HashMap<String,Object>();
		
		MapObj.put("name", EmpName);
		MapObj.put("salary", EmpSalary);

		request = RestAssured.given();
		
		response = request
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(MapObj)
				.post("employees");

		jpath = response.jsonPath();
		EmpId = jpath.get("id");
		return response;

	}

	public Response UpdateEmployee(int EmpID , String EmpName , int EmpSalary) {
		RestAssured.baseURI = this.baseURI;
		
		MapObj = new HashMap<String,Object>();
		
		MapObj.put("name", EmpName);
		MapObj.put("salary", EmpSalary);
		request = RestAssured.given();
		
		response = request
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(MapObj)
				.put("employees/"+EmpID);


		return response;

	}

	public Response DeleteEmployee(int EmpID ) {
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		response = request.delete("employees/"+EmpID);

		return response;

	}



}
