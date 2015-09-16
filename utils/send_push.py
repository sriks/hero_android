import json,httplib

f = open("params.json", "r")
params = json.load(f)
f.close()

connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()
connection.request('POST', '/1/push', json.dumps({
    "where": {
        "channels": params["to"]
    },
    "data": {
        # "alert": params["alert"],
        "a": params["a"],
        "content-available": 1
    }
}), {
    "X-Parse-Application-Id": params["X-Parse-Application-Id"],
    "X-Parse-REST-API-Key": params["X-Parse-REST-API-Key"],
    "Content-Type": "application/json"
})
result = json.loads(connection.getresponse().read())
print result
