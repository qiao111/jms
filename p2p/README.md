1、本例子主要是点对点的请求/应答模式：贷款申请者发送贷款申请请求到贷款申请队列，审核者从贷款申请队列中获取贷款申请信息并将响应消息发送到贷款响应队列中，贷款申请者获取响应队列的内容。
	
	贷款申请------> LoanRequestQueue ------> 审核者 ------> LoanResponseQueue 
	  |													  ^
	  |________________________________________________________|
	  
2、本例子中主要使用的是ActiveMQ。