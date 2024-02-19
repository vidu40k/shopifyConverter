## Build and Deployment Scripts

Этот репозиторий содержит скрипты для сборки и развертывания проекта **shopifyConverter**. Ниже приведены шаги по сборке и развертыванию проекта с нуля.

### 1. Подключение по SSH и Обновление зависимостей
```
ssh username@your_remote_server_ip

sudo apt update
```

### Установка Git (если не установлен)

```
sudo apt install git
```


### Установка Docker (если не установлен)

```
sudo apt install docker.io
```

### 2. Клонирование репозитория
```
git clone https://github.com/vidu40k/shopifyConverter.git

cd shopifyConverter
```


### 3. Сборка Docker образа

```
docker build . -t converter/revit:latest
```

### 4. Запуск контейнера Docker
```
docker run -it -p 8080:8080 converter/revit:latest

```

После выполнения всех шагов ваш проект будет успешно собран и развернут на удаленном сервере
