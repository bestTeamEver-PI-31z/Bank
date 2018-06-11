var dateRegex = /^((0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)?[0-9]{2})*$/;
var moneyRegex = /^\$?[\d,]+(\.\d*)?$/;
var accountNumberRegex = /^[0-9 ]+$/;
var passwordLength = 3;

function validatePassword(input, focus) {
	return validateInput(input, input.val().length >= passwordLength, focus);
}

function validatePasswords(input1, input2, focus) {
	return validateInput(input2, input1.val() == input2.val(), focus);
}

function validateDate(input, focus) {
	return validateInput(input, dateRegex.test(input.val()) && input.val().length != 0, focus);
}

function validateMoney(input, focus) {
	return validateInput(input, moneyRegex.test(input.val()) && input.val().length != 0, focus);
}

function validateAccountNumber(input, focus) {
	return validateInput(input, accountNumberRegex.test(input.val()) && input.val().length > 16, focus);
}

function validateInput(input, condition, focus) {
	if(condition) {
		input.removeClass("is-invalid");
		return true;
	} else {
		input.addClass("is-invalid");
		if(focus) {
			input.focus();
		}
		return false;
	}
}

function toDate(str) {
	var dateSplitter = str.split("/");
	return new Date(dateSplitter[2], dateSplitter[0] - 1, dateSplitter[1]);
}
