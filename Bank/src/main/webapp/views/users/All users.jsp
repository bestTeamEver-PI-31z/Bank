<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<h3 style="margin: 10px;">All users</h3>
<table class="table table-hover table-inverse">
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Roles</th>
			<th>Date of birth</th>
			<th>Phone number</th>
			<th>Number of accounts</th>
			<th>Total balance</th>
			<th>Delete</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${users}">
			<tr>
				<th>${user.id}</th>
				<td><a href="/users/${user.id}">${user.firstName} ${user.lastName}</a></td>
				<th>${user.roles}</th>
				<th><fmt:formatDate pattern="MM/dd/yyyy" value="${user.dateOfBirth}" /></th>
				<th>${user.phoneNumber}</th>
				<th>${user.numberOfAccounts}</th>
				<th>${user.totalBalance}</th>
				<td><a href="" onclick="removeUser('/users/${user.id}');">[delete]</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<div class="msg" style="display: none;">
	<div class="alert alert-danger" role="alert">Cannot remove user, because he has money on account(s)!</div>
</div>
<a href="/register">Register a new user</a>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script>
	$(document).ready(function() {
		let searchParams = new URLSearchParams(window.location.search);
		if(searchParams.has('error')) {
			$(".msg").show();
		}
	});
	function removeUser(url) {
		$.ajax({
			  url: url,
			  type: 'DELETE',
			  success: function(data) {
				  location.href = data;
			  }
		});
	}
</script>
