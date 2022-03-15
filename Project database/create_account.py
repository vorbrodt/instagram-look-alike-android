from flask import *
from flask_sqlalchemy import SQLAlchemy
import json
from data_base import *
import bcrypt

"""
Post a new user to the data base
"""
@app.route("/create_account" , methods = ["POST"])
def create_account():
    email_reg = request.form.get('email')
    username_reg = request.form.get('username')
    password_reg = request.form.get('password')
    salt = bcrypt.gensalt()
    hased_password = bcrypt.hashpw(str(password_reg).encode('utf8'),salt)


    if not email_reg or not username_reg or not hased_password:
        return "",400

    if login_deatails_db.query.filter_by(email = email_reg).all() or User_db.query.filter_by(username = username_reg).all():
        return "",400

    #Creating the object and adding it to the database
    insert_email_and_password = login_deatails_db(email=email_reg, password=hased_password,salt=salt)
    db.session.add(insert_email_and_password)
    insert_username =  User_db(username = username_reg)
    db.session.add(insert_username)
    db.session.commit()

    #add relation betwen the user and User_dB
    parent = login_deatails_db.query.filter_by(email = email_reg).first()
    child = User_db.query.filter_by(username = username_reg).first()
    parent.user_info.append(child)
    db.session.commit()
    return "",200
