<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:choose>
	<c:when test="${user != null}">
		<ul class="nav nav-tabs" id="userTabs" role="tablist">
			<li class="nav-item">
				<a class="nav-link active" href="/users/${user.id}">Info</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="/users/${user.id}/accounts">Accounts<c:if test="${totalBalance != null && totalBalance != 0}"> (<b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${totalBalance}"/></b>)</c:if></a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="/users/${user.id}/transfer">Transfer</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="/users/${user.id}/deposit">Deposit</a>
			</li>
		</ul>
		<div class="user-content">
			<div class="user-info-item">
				<div class="user-info-title">User ID</div><div class="user-info-value">${user.id}</div>
			</div>
			<div class="user-info-item">
				<div class="user-info-title">Phone number</div><div class="user-info-value">${user.phoneNumber}</div>
			</div>
			<div class="user-info-item">
				<div class="user-info-title">First name</div><div class="user-info-value">${user.firstName}</div>
			</div>
			<div class="user-info-item">
				<div class="user-info-title">Last name</div><div class="user-info-value">${user.lastName}</div>
			</div>
			<div class="user-info-item">
				<div class="user-info-title">Date of birth</div><div class="user-info-value">${user.dateOfBirth}</div>
			</div>
			<div class="user-info-item">
				<div class="user-info-title">Address</div><div class="user-info-value">${user.address}</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="msg">
			<div class="alert alert-danger" role="alert">
				User not found!
			</div>
		</div>
	</c:otherwise>
</c:choose>
