package io.ku8.docker.registry;

import org.apache.http.Header;

/**
 * For HTTP invocation returns as a result, including return code,return header array, as well as the content of the return (string)
 * 
 * @author wuzhih
 *
 */
public class HTTPCallResult {
	private int code;
	private Header[] header;
	private String content;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "HTTPCallResult [code=" + code + ", content=" + content + "]";
	}

	public Header[] getHeader() {
		return header;
	}

	public void setHeader(Header[] header) {
		this.header = header;
	}

}
