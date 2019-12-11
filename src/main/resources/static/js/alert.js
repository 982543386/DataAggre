$.notify.addStyle('a_success', {
  html: 
    "<div>" +
		"<div class='alert alert-success alert-dismissible alert-right'>" +
			"<button type='button' class='close' data-dismiss='alert'>×</button>" +
			"<h4><i class='icon fa fa-check'></i><span data-notify-text='title'/></h4>" +
			"<span data-notify-html='message'/>" +
		"</div>"+
    "</div>"
});

$.notify.addStyle('a_error', {
  html: 
    "<div>" +
		"<div class='alert alert-danger alert-dismissible alert-right'>" +
			"<button type='button' class='close' data-dismiss='alert'>×</button>" +
			"<h4><i class='icon fa fa-warning'></i><span data-notify-text='title'/></h4>" +
			"<span data-notify-html='message'/>" +
		"</div>"+
    "</div>"
});

function alertSuccess(_title, _message)
{
	$.notify({
	  title: _title,
	  message: _message
	}, { 
	  style: 'a_success',
	  autoHide: true,
	  clickToHide: true,
	  position: 'bottom right',
	  autoHideDelay: 3000,
	});
}

function alertError(_title, _message)
{
	$.notify({
	  title: _title,
	  message: _message
	}, { 
	  style: 'a_error',
	  autoHide: false,
	  clickToHide: false,
	  position: 'bottom right'
	});
}