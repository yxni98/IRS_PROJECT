import rpa as t
import time, re, json, requests

dic = dict()
for line in open('ML100K_details.csv', 'r', errors = 'surrogateescape'):
	row = line.split(',')
	dic[row[0]] = [row[1].replace('\n', '')]

names = []
urls = []

t.close()
name = 'Toy Story (1995)'
try:
    t.init()
    t.url('https://www.imdb.com')
    for key in dic.keys():
    	t.type('//*[@id="suggestion-search"]', '[clear]')
    	name = dic[key][0]
    	try:
    		t.type('//*[@id="suggestion-search"]', name)   
    		while True:
    			data = str(t.read('page'))
    			if 'img alt=\"'+name.split(' ')[0] in data:
    				img = re.findall(r'searchMenu(.*?)\.jpg\"', data)[0]
    				img = re.findall(r'src=\"(.*?)\.jpg', img+'.jpg')[0]
    				url = img+'.jpg'
    				dic[key].append(url)
    				names.append(name)
    				urls.append(url)
    				break
    	except Exception as e:
    		print(key)
    	
finally:
    t.close()

for i in range(len(names)):
	r = requests.get(urls[i])
	with open('poster/'+names[i]+'.jpg', 'wb') as f:
	    f.write(r.content)