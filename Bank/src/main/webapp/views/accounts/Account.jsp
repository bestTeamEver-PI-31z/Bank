<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="nav nav-tabs" id="userTabs" role="tablist">
	<li class="nav-item">
		<a class="nav-link" href="/users/${userId}">Info</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/users/${userId}/accounts">Accounts<c:if test="${totalBalance != null && totalBalance != 0}"> (<b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${totalBalance}"/></b>)</c:if></a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/users/${user.id}/transfer">Transfer</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/users/${user.id}/deposit">Deposit</a>
	</li>
</ul>
<div class="user-content">
	<div class="nav flex-column nav-pills" id="v-pills-tab" role="tablist" aria-orientation="vertical" style="float: left;">
		<c:forEach items="${accounts}" var="acc" varStatus="loop">
	  		<a class="nav-link<c:if test="${acc.id == account.id}"> active</c:if>" href="/users/${userId}/accounts/${acc.id}">
	  			<div>Number: ${acc.formatedAccountNumber}</div>
	  			<div>Balance: $<fmt:formatNumber type="number" maxFractionDigits="2" value="${acc.balance}"/></div>
	  		</a>
  		</c:forEach>
  		<a class="nav-link<c:if test="${accounts.isEmpty() || addNew}"> active</c:if>" href="/users/${userId}/accounts/new">Open account</a>
	</div>
	<div class="account-content">
	  	<c:choose>
			<c:when test="${account != null && !addNew}">
				<div class="account-info">
		  			<span><strong>Number:</strong> <b>${account.formatedAccountNumber}</b></span>
		  			<a href="/users/${userId}/accounts/${account.id}/delete">[close account]</a>
		  			<span style="float:right;"><strong>Balance:</strong> <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${account.balance}"/></b></span>
		  		</div>
		  		<div class="history">
		  			<h3 style="text-align: center;">History</h3>
			  		<form action="/users/${userId}/accounts/${account.id}" method="GET" class="date-filter">
						<div class="form-group">
							<input type="text" class="form-control" name="fromDate" id="fromDate" placeholder="From date" value="${fromDate}" required />
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="toDate" id="toDate" placeholder="To date" value="${toDate}" required />
						</div>
						<button type="submit" class="btn btn-primary" onclick="return valid();">Filter</button>
						<c:if test="${fromDate != null && toDate != null}">
							<a href="/users/${userId}/accounts/${account.id}" class="btn btn-outline-primary">Remove filter</a>
						</c:if>
					</form>
			  		<c:choose>
			  			<c:when test="${history == null || history.isEmpty()}">
			  				<div class="msg">
								<div class="alert info" role="alert">
									You don't have transfers on this account<c:if test="${fromDate != null && toDate != null}"> in this dates range</c:if>!
								</div>
							</div>
			  			</c:when>
						<c:otherwise>
							
						</c:otherwise>
					</c:choose>
		  			<c:forEach items="${history}" var="historyItem">
				  		<div class="history-item">
				  			<h6><span><fmt:formatDate pattern="MM/dd/yyyy (HH:mm:ss)" value="${historyItem.date}" /></span><span style="float: right;">${historyItem.id}</span></h6>
				  			<c:choose>
								<c:when test="${historyItem.accountNumberFrom == account.id}">
									<div>Sent <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.amount}"/></b> ${historyItem.descriptionTo}</div>
									<div>Balance before transfer: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferFrom}"/></b></div>
									<div>Balance after transfer: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferFrom - historyItem.amount}"/></b></div>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${historyItem.accountNumberTo == account.id && historyItem.accountNumberFrom == 0}">
											<div>Deposit <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.amount}"/></b> ${historyItem.descriptionTo}</div>
											<div>Balance before deposit: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferTo}"/></b></div>
											<div>Balance after deposit: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferTo + historyItem.amount}"/></b></div>
										</c:when>
										<c:otherwise>
											<div>Got <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.amount}"/></b> ${historyItem.descriptionFrom}</div>
											<div>Balance before transfer: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferTo}"/></b></div>
											<div>Balance after transfer: <b>$<fmt:formatNumber type="number" maxFractionDigits="2" value="${historyItem.balanceBeforeTransferTo + historyItem.amount}"/></b></div>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
				  		</div>
			  		</c:forEach>
		  		</div>
			</c:when>
			<c:otherwise>
				<div class="tab-pane fade<c:if test="${accounts.isEmpty() || addNew}"> active</c:if> show" id="create-account-profile" role="tabpanel" aria-labelledby="create-account-profile-tab">
		  			<h4>Open account</h4>
		  			<form action="/users/${user.id}/accounts" method="POST">
		  				<div class="form-group">
							<label for="amount">Amount</label>
							<input type="text" class="form-control" name="amount" id="amount" placeholder="Amount" maxlength="13" required>
						</div>
		  				<button type="submit" class="btn btn-primary" onclick="return validateMoney($('#amount'), true)">Open</button>
		  			</form>
		  		</div>
			</c:otherwise>
		</c:choose>
	</div>
	<div style="clear:both;"></div>
</div>
<link href="https://cdn.jsdelivr.net/npm/gijgo@1.9.6/css/gijgo.min.css" rel="stylesheet" type="text/css" />
<script src="https://cdn.jsdelivr.net/npm/gijgo@1.9.6/js/gijgo.min.js" type="text/javascript"></script>
<script>
	$('#fromDate').datepicker({
		uiLibrary : 'bootstrap4'
	});
	$('#toDate').datepicker({
		uiLibrary : 'bootstrap4'
	});
	function valid() {
		if(validateDate($('#fromDate'), true)) {
			if(validateDate($('#toDate'), true)) {
				if(validateInput($('#toDate'), toDate($('#fromDate').val()) <= toDate($('#toDate').val()), true)) {
					return true;
				}
			}
		}
		return false;
	}
</script>
<style>
	.form-control:focus {
		color: #495057;
		background-color: #fff;
		border-color: #80bdff;
		outline: 0;
		box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, .25);
	}
</style>
