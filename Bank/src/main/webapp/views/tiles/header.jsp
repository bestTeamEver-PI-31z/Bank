<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-light header" style="background-color: #e3f2fd;">
	<a class="navbar-brand" href="/">Bank</a>
	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor03" aria-controls="navbarColor03" aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>
	<div class="collapse navbar-collapse" id="navbarColor03">
		<ul class="navbar-nav mr-auto">
			<sec:authorize access="hasRole('ADMIN')">
				<li class="nav-item">
					<a class="nav-link" href="/users">Users</a>
				</li>
			</sec:authorize>
		</ul>
		<div class="login-register">
			<sec:authorize access="isAuthenticated()">
				<div class="nav-item dropdown">
			        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			        	Hi, ${principalUser.firstName} ${principalUser.lastName}
			        </a>
			        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
			        	<a class="dropdown-item" href="/users/${principalUser.id}">My Account</a>
			        	<div class="dropdown-divider"></div>
			        	<a class="dropdown-item" href="/logout">Sign out</a>
			        </div>
			    </div>
			</sec:authorize>
			<sec:authorize access="isAnonymous()">
				<a href="/login">Sign in</a> or <a href="/register">Sign up</a>
			</sec:authorize>
		</div> 
	</div>
</nav>
