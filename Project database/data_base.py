from flask import *
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager, jwt_required
from flask_restful import Resource
from flask_jwt_extended import create_access_token
from flask_jwt import JWT
import os

if 'NAMESPACE' in os.environ and os.environ['NAMESPACE'] == 'heroku':
    db_uri = os.environ['DATABASE_URL']
    debug_flag = False
else: # when running locally with sqlite
    db_path = os.path.join(os.path.dirname(__file__), 'app.db')
    db_uri = 'sqlite:///{}'.format(db_path)
    debug_flag = True



app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///memer00m.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JWT_SECRET_KEY'] = 'robin gillar att ha hicka'
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = 604800

class Endpoint(Resource):
    @jwt_required
    def get(self):
        return {"State": "Success"}

db = SQLAlchemy(app)
jwt = JWTManager(app)

"""
relations
"""

#relationen between Login and User
user_info = db.Table('user_info',
    db.Column('user_username', db.Integer, db.ForeignKey('user.username')),
    db.Column('login_email', db.Integer, db.ForeignKey('login.email'))
)

#relationen between Login and upload
upload_info = db.Table('upload_info',
    db.Column('upload_meme', db.Integer, db.ForeignKey('upload.meme')),
    db.Column('login_email', db.Integer, db.ForeignKey('login.email'))
)
"""
relations end start of db
"""
class login_deatails_db(db.Model):
    __tablename__ = "login"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(140), unique=True, nullable=False)
    password = db.Column(db.String(140))
    salt = db.Column(db.String,nullable=False)

    user_info = db.relationship('User_db', secondary=user_info,backref=db.backref('email', lazy=True))

    upload_info = db.relationship('Upload_db', secondary=upload_info,backref=db.backref('email', lazy="dynamic"))

    def __init__(self, email, password, salt):
        self.email = email
        self.password = password
        self.salt=salt
    def __repr__(self):
        return str((self.email, self.password))
"""
end of login_deatails_db start of Users_db
"""

class User_db(db.Model):
    __tablename__ = "user"
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(140), unique=True, nullable=False)

    time = db.Column(db.String)
    newest_meme = db.Column(db.String)
    location = db.Column(db.String(140))

    profile_picture = db.Column(db.String())
    user_info = db.relationship('login_deatails_db', secondary=user_info,backref=db.backref('username', lazy=True))

    #relations, one-to-many
    followers = db.relationship('Followers_db', backref='followers', lazy=True)
    following = db.relationship('Following_db', backref='following', lazy=True)


    def __repr__(self):
        return str(self.username)
"""
end of User_db start of child dbs
"""
class Followers_db(db.Model):
    __tablename__ = "follower"
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String)
    followers_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
    def __repr__(self):
        return str(self.username)
        
class Following_db(db.Model):
    __tablename__ = "following_this"
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String)
    following_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
    def __repr__(self):
        return str(self.username)

"""
end of child dbs start of Upload_db
"""

class Upload_db(db.Model):
    __tablename__ = "upload"
    id = db.Column(db.Integer, primary_key=True)

    memeTitle = db.Column(db.String)
    meme = db.Column(db.String,unique=True)

    comments = db.relationship('Comment_db', backref='memes', lazy=True)
    likes = db.relationship('Like_db', backref="memes", lazy=True)
    dislikes = db.relationship('Dislike_db',backref=db.backref('memes', lazy=True))


    def __repr__(self):
        return str(self.meme)
"""
end of Upload_db start of child dbs
"""

class Comment_db(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String)
    text_comment = db.Column(db.String(255), unique=False, nullable=False)
    memes_id = db.Column(db.Integer, db.ForeignKey("upload.id"), nullable=False)
    def __repr__(self):
        return str({'Msg': str(self.text_comment),'user': str(self.username)})


class Like_db(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String, unique=False, nullable=False)
    memes_id = db.Column(db.Integer, db.ForeignKey("upload.id"), nullable=False)
    def __repr__(self):
        return str(self.email)


class Dislike_db(db.Model):
    __tablename__ = "dislike"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String, unique=False, nullable=False)
    memes_id = db.Column(db.Integer, db.ForeignKey("upload.id"), nullable=False)
    def __repr__(self):
        return str(self.email)


"""
end of child dbs Start of Blacklisted Tokens
"""
class Blacklist_db(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    token = db.Column(db.String(255),nullable=False)
    def __repr__(self):
        return str(self.token)



"""
Tree of how the databes works



____________________________login_deatails_db___________________________________
                /                                       \
_____________User_db________________________________Upload_db___________________
        /               \                       /                   \
__Follwers_db_______Following_db__________Comments_db__________Like_db__________

"""
