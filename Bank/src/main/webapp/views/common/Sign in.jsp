<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<form action="/login" method="POST" class="login">
	<h3>Sign in</h3>
	<div class="msg" style="display: none;"><div class="alert alert-danger" role="alert">Invalid username or password</div></div>
	<div class="form-group">
		<label for="username">Phone Number</label>
		<input type="text" class="form-control" name="username" id="username" placeholder="Phone Number" required autofocus />
	</div>
	<div class="form-group">
		<label for="password">Password</label>
		<input type="password" class="form-control" name="password" id="password" placeholder="Password" required />
	</div>
	<button type="submit" class="btn btn-primary">Sign in</button>
</form>
<script>
	$(document).ready(function() {
		let searchParams = new URLSearchParams(window.location.search);
		if(searchParams.has('error')) {
			$(".msg").show();
		}
	});
</script>