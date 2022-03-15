import os
import tempfile
import pytest
from data_base import app
import server
import json
from flask import request

#user one
email = "Testar@gmail.com"
username = "Tester"
token = ""
email2 = "Testar2@gmail.com"
username2 = "Tester2"
token2 = ""
@pytest.fixture
def client():
    db_fd, app.config['DATABASE_FILE_PATH'] = tempfile.mkstemp()
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + app.config['DATABASE_FILE_PATH']
    app.config['TESTING'] = True

    client = app.test_client()

    with app.test_request_context():
        server.init_db()

    yield client

    os.close(db_fd)
    os.unlink(app.config['DATABASE_FILE_PATH'])

def test_start_db(client):
    r = client.get('/init_db')
    assert b'DATABASE' in r.data

def test_empty_db(client):
    r = client.get('/')
    assert b'Welcome!' in r.data

def test_everything(client):
    """
    Test create account and bad request
    """
    payload = {'email': email,'password':'code12345',"username":username}
    r = client.post('/create_account',data = payload)
    assert '200 OK' in str(r.status)

    payload = {'email': email2,'password':'code12345',"username":username2}
    r = client.post('/create_account',data = payload)
    assert '200 OK' in str(r.status)

    payload = {'email': email,'none':'code12345',"username":username}
    r = client.post('/create_account',data = payload)
    assert '400 BAD REQUEST' in str(r.status)


    """
    Test Login and bad request
    """
    payload = {'email': email,'password':'code12345'}
    r = client.post('/login' , data = payload)
    token = list(json.loads(r.data.decode(encoding='utf-8')).values())[0]
    assert '200 OK' in str(r.status)

    payload = {'email': email,'password':'Wrong'}
    r = client.post('/login' , data = payload)
    assert '400 BAD REQUEST' in str(r.status)

    """
    Test uppload and bad request
    """
    payload={"email":email,"memePic":"Random","memeTitle":"Some Title"}
    r = client.post('/upload/meme' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '200 OK' in str(r.status)

    payload={"email":email,"memePic":"Random","memeTitle":"Some Title"}
    r = client.post('/upload/meme' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '400 BAD REQUEST' in str(r.status)

    payload={"wrong":email,"memePic":"Random","wrong":"Some Title"}
    r = client.post('/upload/meme' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '400 BAD REQUEST' in str(r.status)

    """
    Follow a user
    and get all that we follow
    """
    payload = {'email': email,'follow' : username2}
    r = client.post('/follow/user' , data = payload, headers = {"Authorization": "Bearer " + token})
    assert '200 OK' in str(r.status)
    r = client.get('/download/following/info/'+email,headers = {"Authorization": "Bearer " + token})
    assert '{"following":"[Tester2]"}' in str(r.data)

    """
    Test Get profile
    and non profile
    """
    r = client.get('/username/search/'+username,headers = {"Authorization": "Bearer " + token})
    assert '200 OK' in str(r.status)
    r = client.get('/username/search/not_a_user',headers = {"Authorization": "Bearer " + token})
    assert '400 BAD REQUEST' in str(r.status)

    """
    Test Display meme
    and comment , and BAD REQUESTS
    """
    payload={"memePic":"Random"}
    r = client.post('/display/meme' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '200 OK' in str(r.status)

    payload={"memePic":"Not_A_Meme"}
    r = client.post('/display/meme' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '400 BAD REQUEST' in str(r.status)

    payload={"email":email,"memePic":"Random","comment":"Nice meme"}
    r = client.post('/post/comment' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '200 OK' in str(r.status)

    payload={"email":email,"memePic":"Not_A_Meme","comment":"Nice meme"}
    r = client.post('/post/comment' , data = payload,headers = {"Authorization": "Bearer " + token})
    assert '400 BAD REQUEST' in str(r.status)
