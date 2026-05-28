# SubsAndBeer 📱

Android-приложение для управления подписками на услуги с офлайн-режимом и синхронизацией данных.

## О проекте

Дипломная работа — мобильное приложение, которое помогает отслеживать подписки, анализировать расходы и не забывать о списаниях. Разработано как альтернатива существующим решениям с поддержкой офлайн-работы и синхронизации между устройствами.

## Функциональность

- Управление подписками: добавление, редактирование, архивирование, удаление
- Аналитика расходов с круговыми диаграммами по категориям, валютам и методам оплаты
- Календарь предстоящих платежей
- Push-уведомления о списаниях (за 1, 2 или 5 дней)
- Офлайн-режим с синхронизацией при восстановлении соединения
- Авторизация по номеру телефона (звонок с кодом)
- Светлая и тёмная тема

## Стек технологий

**Клиент (Android)**
- Kotlin, Jetpack Compose, MVVM
- Room (локальная БД), Retrofit (HTTP-клиент)
- WorkManager (фоновая синхронизация и уведомления)
- Hilt (DI), EncryptedSharedPreferences (хранение JWT)

**Сервер** (отдельный репозиторий)
- Python, Flask, MySQL
- JWT-аутентификация

## Архитектура

Клиент-серверная система с паттерном MVVM на клиенте. Поддерживает инкрементальную синхронизацию — при изменении данных локально они помечаются как `pending` и отправляются на сервер при наличии соединения.

## Скриншоты

### Авторизация
<p float="left">
  <img src="screenshots/экран авторизации.png" width="200"/>
  <img src="screenshots/экран авторизации светлой фон.png" width="200"/>
  <img src="screenshots/подтверждения номера телефона.png" width="200"/>
</p>

### Подписки
<p float="left">
  <img src="screenshots/Активные подписки.png" width="200"/>
  <img src="screenshots/Архивные подписки.png" width="200"/>
  <img src="screenshots/экран подписки.png" width="200"/>
</p>

### Добавление подписки
<p float="left">
  <img src="screenshots/шаблоны подписок.png" width="200"/>
  <img src="screenshots/категории шаблонов.png" width="200"/>
  <img src="screenshots/новая подписка 1.png" width="200"/>
  <img src="screenshots/новая подписка 2.png" width="200"/>
  <img src="screenshots/новая подписка 3.png" width="200"/>
  <img src="screenshots/новая подписка шаблон.png" width="200"/>
</p>

### Дополнительные элементы интерфейса
<p float="left">
  <img src="screenshots/стиль иконки.png" width="200"/>
  <img src="screenshots/bottom sheet уведомления.png" width="200"/>
  <img src="screenshots/bottom sheet Валюта.png" width="200"/>
  <img src="screenshots/датапикер.png" width="200"/>
  <img src="screenshots/timePicker.png" width="200"/>
</p>

### Аналитика
<p float="left">
  <img src="screenshots/экран аналитики.png" width="200"/>
  <img src="screenshots/экран аналитики 1.png" width="200"/>
  <img src="screenshots/экран аналитики 2.png" width="200"/>
  <img src="screenshots/экран графика самых дорогих подписок.png" width="200"/>
  <img src="screenshots/распределение по валютам.png" width="200"/>
  <img src="screenshots/распределение по валютам оба.png" width="200"/>
  <img src="screenshots/Валюта RUB.png" width="200"/>
  <img src="screenshots/Валюта RUB светлая.png" width="200"/>
</p>

### Профиль и настройки
<p float="left">
  <img src="screenshots/экран профиля.png" width="200"/>
  <img src="screenshots/экран профиля пользователя.png" width="200"/>
  <img src="screenshots/Экран Категории.png" width="200"/>
  <img src="screenshots/Экран Платежные методы.png" width="200"/>
  <img src="screenshots/добавить категорию.png" width="200"/>
  <img src="screenshots/добавить платежный метод.png" width="200"/>
  <img src="screenshots/выбор валюты по умолчанию.png" width="200"/>
  <img src="screenshots/TimePickerDialog.png" width="200"/>
</p>


## Требования

- Android 8.0 (API 26) и выше
- RAM от 2 ГБ
