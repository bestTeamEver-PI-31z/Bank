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
		<a class="nav-link active" href="/users/${user.id}/transfer">Transfer</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/users/${user.id}/deposit">Deposit</a>
	</li>
</ul>
<div class="user-content">
	<c:choose>
		<c:when test="${accounts != null && !accounts.isEmpty()}">
			<form action="/users/${user.id}/transfer" method="POST" class="transfer">
				<div class="msg">
					<c:if test="${error != null && !error.isEmpty()}">
						<div class="alert alert-danger" role="alert">
							${error}
						</div>
					</c:if>
					<c:if test="${success != null && success}">
						<div class="alert alert-success" role="alert">
							You have been successfully transfered money
						</div>
					</c:if>
					<c:if test="${info != null}">
						<div class="alert info" role="alert">
							${info}
						</div>
					</c:if>
				</div>
				<input type="hidden" name="redirectUrl" value="${redirectUrl}">
				<div class="form-group">
					<label for="from">From an account</label>
					<select name="from" class="form-control" id="from" required>
						<c:forEach items="${accounts}" var="account" varStatus="loop">
							<option value="${account.id}" balance="${account.balance}" <c:if test="${selectedAccountId != null && selectedAccountId == account.id}">selected="selected"</c:if>>
									$<fmt:formatNumber type="number" maxFractionDigits="2" value="${account.balance}" />; ${account.shortAccountNumber}
							</option>
				  		</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="to">To the account</label>
					<select name="to" class="form-control" id="to" required>
						<option value="0">Manual entering</option>
						<c:forEach items="${accounts}" var="account" varStatus="loop">
							<option value="${account.id}">
									$<fmt:formatNumber type="number" maxFractionDigits="2" value="${account.balance}" />; ${account.shortAccountNumber}
							</option>
				  		</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="accountNumber">Enter the account number</label>
					<input type="text" class="form-control" name="accountNumber" id="accountNumber" placeholder="Account number" maxlength="20" />
				</div>
				<div class="form-group">
					<label for="amount">Amount</label>
					<input type="text" class="form-control" name="amount" id="amount" placeholder="Amount" maxlength="13" />
				</div>
				<button type="submit" class="btn btn-primary" onclick="return valid()">Transfer</button>
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
<script>
    var hasSelectedAccountId = '${selectedAccountId != null}';
	$(document).ready(function() {
	    $("#to").on("change", function() {
	    	if($(this).val() == "0") {
	    		$("#accountNumber").parent().show();
	    	} else {
	    		$("#accountNumber").parent().hide();
	    	}
	    });
	    if(hasSelectedAccountId == "true") {
	    	$("#amount").val(parseFloat($("#from option:selected").attr("balance")));
	    }
	});
	function valid() {
		if($("#accountNumber").parent().is(":visible")) {
			if(validateAccountNumber($("#accountNumber"), true)) {
				return valF();
			}
		} else {
			return valF();
		}
		return false;
	}
	function valF() {
		if(validateMoney($("#amount"), true)) {
			var amt = parseFloat($("#from").find(":selected").attr("balance"));
			if(validateInput($("#amount"), amt >= parseFloat($("#amount").val()), true)) {
				return true;
			}
		}
		return false;
	}
</script>
