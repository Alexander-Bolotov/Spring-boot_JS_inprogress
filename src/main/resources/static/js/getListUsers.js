$(document).ready(function() {
    $.getJSON('/list', function(json) {
        let tr=[];
        for (let i = 0; i < json.length; i++) {
            tr.push('<tr>');
            tr.push('<td>' + json[i].id + '</td>');
            tr.push('<td>' + json[i].name + '</td>');
            tr.push('<td>' + json[i].password + '</td>');
            tr.push('<td>' + json[i].roles.map(role => role.role) + '</td>');
            tr.push('<td><button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal" data-from=\'json[i].id\'>UPDATE</button>&nbsp;&nbsp;<button class=\'delete\' id=' + json[i].id + '>Delete</button></td>');
            tr.push('</tr>');
        }
        $("#tableUserList").append($(tr.join('')));
    });

    $(document).delegate('.delete', 'click', function() {
        if (confirm('Do you really want to delete this User?')) {
            let id = $(this).attr('id');
            let parent = $(this).parent().parent();
            $.ajax({
                type: "DELETE",
                url: "/delete?id=" + id,
                cache: false,
                success: function() {
                    parent.fadeOut('slow', function() {
                        $(this).remove();
                    });
                    location.reload(true)
                },
                error: function() {
                    $('#err').html('<span style=\'color:red; font-weight: bold; font-size: 30px;\'>Error deleting record').fadeIn().fadeOut(4000, function() {
                        $(this).remove();
                    });
                }
            });
        }
    });

    $(document).delegate('.edit', 'click', function() {
        let parent = $(this).parent().parent();

        let id = parent.children("td:nth-child(1)");
        let name = parent.children("td:nth-child(2)");
        let buttons = parent.children("td:nth-child(3)");

        name.html("<input type='text' id='txtName' value='" + name.html() + "'/>");
        buttons.html("<button id='save'>Save</button>&nbsp;&nbsp;<button class='delete' id='" + id.html() + "'>Delete</button>");
    });


    $(document).ready(function() {

        $('.myModal').click(function(event){
            event.preventDefault();
            $('#overlay').fadeIn(400, // анимируем показ обложки
                function(){ // далее показываем мод. окно
                    $('#myModal')
                        .css('display', 'block')
                        .animate({opacity: 1, top: '50%'}, 200);
                });
        });

        // закрытие модального окна
        $('#modal_close, #overlay').click( function(){
            $('#myModal')
                .animate({opacity: 0, top: '45%'}, 200,  // уменьшаем прозрачность
                    function(){ // пoсле aнимaции
                        $(this).css('display', 'none'); // скрываем окно
                        $('#overlay').fadeOut(400); // скрывaем пoдлoжку
                    }
                );
        });

    });

    $( document ).ready(function() {
        $("#addUserBtn").click(
            function(){
                sendAjaxForm('result_form', 'ajax_form', '/addUser');
                return false;
            }
        );
    });

    function sendAjaxForm(result_form, ajax_form, url) {
        jQuery.ajax({
            url:     url, //url страницы (action_ajax_form.php)
            type:     "GET", //метод отправки
            dataType: "html", //формат данных
            data: jQuery("#"+ajax_form).serialize(),  // Сеарилизуем объект
            success: function(response) { //Данные отправлены успешно
                let result = jQuery.parseJSON(response);
                document.getElementById(result_form).innerHTML = "Имя: "+result.name+"<br>Password: "+result.password;
            },
            error: function(response) { // Данные не отправлены
                document.getElementById(result_form).innerHTML = "Ошибка. Данные не отправленны.";
            }
        });
    }


});