package com.example.randomapp;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Объявляем объекты классов для надписей и поля ввода текста
    private TextView txtComment, txtGeneratedNumber;
    private EditText editInput;
    // Объявляем переменные для хранения вводимого пользователем числа и сгенерированного числа
    int userNumber, generatedNumber;
    // Объявляем объект класса Random, генерирующий случайное число
    final Random random = new Random();
    // Создаём переменную и указываем идентификатор канала
    private static final String CHANNEL_ID = "CHANNEL_ID";
    // Объявляем новый объект класса NotificationManager
    private NotificationManager notificationManager;
    // Объявляем значение идентификатора уведомления
    private static final int NOTIFY_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Определяем элементы интерфейса с помощью метода findViewById()
        txtComment = findViewById(R.id.txtComment);
        txtGeneratedNumber = findViewById(R.id.txtGeneratedNumber);
        editInput = findViewById(R.id.editInput);
        // Определяем контекст объекта класса NotificationManager
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE) ;
    }

    // Реализуем метод, в котором будет храниться код всплывающего сообщения
    public void showToast() {
        // Задаём содержание всплывающего сообщения и выводим его
        Toast.makeText(getApplicationContext(), "Чтобы получить новое число, " + "нажмите на кнопку еще раз.", Toast.LENGTH_LONG).show();
    }
    // Создаём новый метод и прописываем реализацию диалогового окна
    public void showDialog(){
        // Создаём "построитель" диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Задаём заголовок
        builder.setTitle("Ошибка")
                // Задаём текст диалогового окна
                .setMessage("Вы забыли ввести число. \n\nПопробуйте еще раз.")
                // Задаём иконку
                .setIcon(R.drawable.ic_alert)
                // Задаём возможность отмены и принудительного закрытия
                .setCancelable(false)
                // Добавляем кнопку "ОК"
                .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
                    // Обрабатываем событие нажатия кнопки
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Создаём диалоговое окно
        AlertDialog alert = builder.create();
        // Отображаем диалоговое окно
        alert.show();
    }

    // Реализуем метод создания канала уведомления, принимающий на выход объект класса NotificationManager
    public static void createNotificationChannel(NotificationManager manager) {
        // Регистрируем канал уведомлений приложения в системе, передав объект класса  NotificationChannel в создание канала уведомлений createNotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
    // Создаём новый метод, в котором будет размещён код, необходимый для создания и запуска уведомления
    public void showNotification() {
        // Создаём объект класса Intent, который будет инициировать переход из текущего контекста к экрану (классу, отвечающему за экран)
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // Для Intent вызываем метод, помогающий сохранить ожидаемую навигацию пользователя после того, как он откроет приложение с помощью уведомления
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Оборачиваем Intent в ещё один Intent - объект класса PendingIntent, получающий тот контекст, из которого был вызван, и выполняющий написанный ранее Intent intent
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Создаём "построитель" уведомления
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                // Задаём иконку
                .setSmallIcon(R.drawable.notification)
                // Задаём заголовок
                .setContentTitle("Уведомление")
                // Задаём текст уведомления
                .setContentText("Сгенерировано новое число!")
                // Задаём время, в которое уведомление будет отображаться
                .setWhen(System.currentTimeMillis())
                // Задаём автоматическое завершение
                .setAutoCancel(false)
                // Применяем pendingIntent к уведомлению
                .setContentIntent(pendingIntent)
                // Задаём высокий приоритет
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Вызываем метод, передав ему на вход созданный менеджер уведомлений
        createNotificationChannel(notificationManager);
        // С помощью метода notify() само уведомление, на основе написанного ранее "построителя" уведомлений
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    // Создаём обработчик события нажатия кнопки
    public void onClickButton(View view) {
        // Реализуем условие, проверяющее скрытность надписи со сгенерированным числом
        if (txtGeneratedNumber.getVisibility() == View.GONE) {
            // Реализуем условие, проверяющее пустоту в поле ввода
            if (editInput.getText().toString().isEmpty()) {
                showDialog();
            // Если пользователь всё же ввёл число, то выполняем следующее:
            } else {
                // Извлекаем число из поля для ввода
                userNumber = Integer.parseInt(editInput.getText().toString());
                // Очищаем поле для ввода текста
                editInput.setText("");
                // Скрываем поле для ввода текста
                editInput.setVisibility(View.GONE);
                // Генерируем случайное число от 0 до введённого значения
                generatedNumber = random.nextInt(userNumber);
                // Изменяем текст надписи на комментарий по заданному диапазону чисел
                txtComment.setText("Ваше число\nот 0 до " + userNumber);
                // Выводим сгенерированное число в тексте надписи
                txtGeneratedNumber.setText(String.valueOf(generatedNumber));
                // Показываем надпись для сгенерированного числа
                txtGeneratedNumber.setVisibility(View.VISIBLE);
                // Вызываем метод
                showToast();
            }
        // Если сгенерированное число отображается, то выполняем следующее:
        } else {
            // Отображаем поле для ввода текста
            editInput.setVisibility(View.VISIBLE);
            // Скрываем надпись для сгенерированного числа
            txtGeneratedNumber.setVisibility(View.GONE);
            // Изменяем текст надписи с комментарием на первоначальный
            txtComment.setText("Задать число\nот 0 до:");
        }
    }
    }