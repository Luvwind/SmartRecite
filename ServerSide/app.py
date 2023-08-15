import json
import os

from flask import Flask, current_app, request, send_from_directory,  jsonify,g
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
import sqlite3


app = Flask(__name__)

app.config['SECRET_KEY'] = os.urandom(24)
#随机生成一个24位加密字符作为登录状态判断token


@app.route('/word', methods=['GET'])
def word():
    return app.send_static_file('word.db')


@app.route('/quote', methods=['GET'])
def quore():
    return app.send_static_file('quote.db')


@app.route('/login', methods=['POST'])
def login():
    username = request.form.get('username')
    password = request.form.get('password')

    if username is not None and password is not None:
        with open("users.json", 'r') as f:
            users = json.load(f)

        for user in users['users']:
            if username == user['username'] and password == user['password']:
                #创建一个序列化器对象加密用户名作为token
                serializer = Serializer(current_app.config["SECRET_KEY"], expires_in=2592000)
                token = serializer.dumps({"id": username}).decode("utf8")
                #返回token为登录成功
                return token

        return "Bad authentication."



@app.route('/register', methods=['POST'])
def register():
    username = request.form.get('username')
    password = request.form.get('password')

    if username is not None and password is not None:
        with open("users.json", 'r') as f:
            users = json.load(f)
        #遍历本地用户数据库
        for user in users['users']:
            if username == user['username']:
                return "Username already exists."

        new_user = {"username": username, "password": password}
        users['users'].append(new_user)
        #写入本地用户数据库
        with open("users.json", 'w') as f:
            json.dump(users, f)


        # 创建用户文件夹，如果不存在
        user_dir = os.path.join('uploads', new_user['username'])
        if not os.path.exists(user_dir):
            os.makedirs(user_dir)


        return "Registration successful."

    return "Invalid input."


@app.route('/api/upload', methods=['POST'])
def upload():
    # 获取客户端返回过的token
    token = request.form.get("token")
    # 创建一个序列化器对象，注意要使用和生成token时相同的密钥
    serializer = Serializer(current_app.config['SECRET_KEY'])
    # 尝试从token中还原原id
    user = serializer.loads(token)


    with open("users.json", 'r') as f:
        users = json.load(f)
    for localid in users['users']:  # 遍历本地用户数据
        #原id存在，验证通过
        if user['id'] == localid['username']:
            file = request.files.get('file')
            # 创建用户文件夹，如果不存在
            user_dir = os.path.join('uploads', user['id'])
            if not os.path.exists(user_dir):
                os.makedirs(user_dir)
            # 保存文件到用户文件夹
            file.save(os.path.join(user_dir, file.filename))
            return "Successfully uploaded."  # 返回成功响应信息

        #    token无效或过期，验证失败
    return "Bad authentication."





@app.route('/api/fetch' , methods=['POST'])
def fetch():
    # 获取客户端返回过的token
    token = request.form.get("token")
    # 创建一个序列化器对象，注意要使用和生成token时相同的密钥
    serializer = Serializer(current_app.config['SECRET_KEY'])
    # 尝试从token中还原原id
    user = serializer.loads(token)

    with open("users.json", 'r') as f:
        users = json.load(f)

    for localid in users['users']:#遍历本地用户数据
            # 原id存在，验证通过
        if user['id'] == localid['username']:
            filename = request.args.get('filename')
            #从个人文件夹中取出自己的配置文件
            return send_from_directory('uploads/'+ user['id'], filename, as_attachment=True)
        # else:
        #     # token无效或过期，验证失败
    return "Bad authentication."


# 获取数据库连接的函数
def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        # 连接数据库，并返回一个数据库对象
        db = g._database = sqlite3.connect('static/classroomnews.db')
    return db

# 关闭数据库连接的函数
@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        # 关闭数据库连接
        db.close()

@app.route("/classroom")
def classroom():
    # 获取数据库连接
    db = get_db()
    # 创建一个游标对象
    cursor = db.cursor()
    # 查询数据表，获取文本和图片数据
    cursor.execute('SELECT * FROM newslist')
    # 获取查询结果，转换为列表格式
    data = cursor.fetchall()
    # 返回json格式的数据
    return jsonify(data)

if __name__ == "__main__":
    app.run()
