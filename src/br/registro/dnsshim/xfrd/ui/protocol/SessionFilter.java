/* Copyright (C) 2009 Registro.br. All rights reserved. 
* 
* Redistribution and use in source and binary forms, with or without 
* modification, are permitted provided that the following conditions are 
* met:
* 1. Redistribution of source code must retain the above copyright 
*    notice, this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in the
*    documentation and/or other materials provided with the distribution.
* 
* THIS SOFTWARE IS PROVIDED BY REGISTRO.BR ``AS IS'' AND ANY EXPRESS OR
* IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIE OF FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
* EVENT SHALL REGISTRO.BR BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
* TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
* USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
* DAMAGE.
 */
package br.registro.dnsshim.xfrd.ui.protocol;

import org.apache.log4j.Logger;

import br.registro.dnsshim.common.server.DnsshimProtocolException;
import br.registro.dnsshim.common.server.IoFilter;
import br.registro.dnsshim.common.server.IoSession;
import br.registro.dnsshim.common.server.ProtocolStatusCode;
import br.registro.dnsshim.xfrd.domain.logic.DnsshimSession;
import br.registro.dnsshim.xfrd.domain.logic.DnsshimSessionCache;

public class SessionFilter implements IoFilter {
	private static final Logger logger = Logger.getLogger(SessionFilter.class);
	
	@Override
	public void filter(Object message, IoSession session)
			throws DnsshimProtocolException {
		if (message instanceof RestrictOperation) {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to execute a restrict operation...");
			}
			RestrictOperation operation = (RestrictOperation) message;
			DnsshimSessionCache sessionCache = DnsshimSessionCache.getInstance();
			DnsshimSession dnsshimSession = sessionCache.get(operation.getSessionId());
			if (dnsshimSession == null) {
				throw new DnsshimProtocolException(ProtocolStatusCode.FORBIDDEN, "Forbidden (not logged in)");
			}
			
			String remoteHost = session.getRemoteAddress().getAddress().getHostAddress();
			String sessionHost = dnsshimSession.getRemoteAddress().getAddress().getHostAddress();
			if (!remoteHost.equals(sessionHost)) {
				throw new DnsshimProtocolException(ProtocolStatusCode.FORBIDDEN, "Forbidden");
			}
		}
	}
}
