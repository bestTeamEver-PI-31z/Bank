<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="nav nav-tabs" id="userTabs" role="tablist">
	<li class="nav-item">
		<a class="nav-link" href="/users/${userId}">Info</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/users/${userId}/accounts">Accounts<c:if test="${totalBalance != null && totalBalance != 0}"> (<b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${totalBalance}"/></b>)</c:if></a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/users/${user.id}/transfer">Transfer</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/users/${user.id}/deposit">Deposit</a>
	</li>
</ul>
<div class="user-content">
	<c:choose>
		<c:when test="${accounts != null && !accounts.isEmpty()}">
			<form action="/users/${user.id}/deposit" method="POST" class="transfer">
				<div class="msg">
					<c:if test="${error != null && !error.isEmpty()}">
						<div class="alert alert-danger" role="alert">
							${error}
						</div>
					</c:if>
					<c:if test="${success != null && success}">
						<div class="alert alert-success" role="alert">
							You have been successfully deposited!
						</div>
					</c:if>
				</div>
				<div class="form-group">
					<label for="account">Select account</label>
					<select name="account" class="form-control" id="account" required>
						<c:forEach items="${accounts}" var="account" varStatus="loop">
							<option value="${account.id}" balance="${account.balance}">
									$<fmt:formatNumber type="number" maxFractionDigits="2" value="${account.balance}" />; ${account.shortAccountNumber}
							</option>
				  		</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="amount">Amount</label>
					<input type="text" class="form-control" name="amount" id="amount" placeholder="Amount" maxlength="13" />
				</div>
				<button type="submit" class="btn btn-primary" onclick="return validateMoney($('#amount'), true);">Deposit</button>
			</form>
		</c:when>
		<c:otherwise>
			<div class="msg">
				<div class="alert info">
					You don't have accounts!
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>
