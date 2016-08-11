from commonrest import *
data = {"id":"2508744128", "timestamp":"2016-08-10T21:27:29.450Z", "isTest":False, "url":"http://datacratic.com/", "language":"en", "exchange":"mock", "location":{"countryCode":"CA", "regionCode":"QC", "cityName":"Montreal", "dma":-1, "metro":-1, "timezoneOffsetMinutes":-1}, "userIds":{"prov":"1568485738", "xchg":"1475615520"}, "imp":[{"id":"1", "formats":["160x600"], "position":0}, {"id":"2", "formats":["160x600"], "position":0}], "spots":[{"id":"1", "formats":["160x600"], "position":0}, {"id":"2", "formats":["160x600"], "position":0}]}
r = postrtb("/bids", 12339, data)
r = postrtb("/win", 12340, {"account":["hello", "world"], "adSpotId":"1", "auctionId":"3438835179", "bidTimestamp":0.0, "channels":[], "timestamp":1470865069.551620, "type":1, "uids":{"prov":"2046608033", "xchg":"678906006"}, "winPrice":[62, "USD/1M"]})
r = postrtb("/win", 12340, {"account":[], "adSpotId":"2", "auctionId":"3802587280", "bidTimestamp":0.0, "channels":[], "label":"CLICK", "timestamp":1470866940.674299, "type":3, "uids":{"prov":"1862382133", "xchg":"3558853724"}, "winPrice":0})