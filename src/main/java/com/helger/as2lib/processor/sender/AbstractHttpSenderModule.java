/**
 * The FreeBSD Copyright
 * Copyright 1994-2008 The FreeBSD Project. All rights reserved.
 * Copyright (C) 2013 Philip Helger ph[at]phloc[dot]com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the FreeBSD Project.
 */
package com.helger.as2lib.processor.sender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.mail.internet.InternetHeaders;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedException;

public abstract class AbstractHttpSenderModule extends AbstractSenderModule
{

  public static final String PARAM_READ_TIMEOUT = "readtimeout";
  public static final String PARAM_CONNECT_TIMEOUT = "connecttimeout";

  @Nonnull
  public HttpURLConnection getConnection (final String sUrl,
                                          final boolean bOutput,
                                          final boolean bInput,
                                          final boolean bUseCaches,
                                          final String sRequestMethod) throws OpenAS2Exception
  {
    try
    {
      final URL aUrlObj = new URL (sUrl);
      final HttpURLConnection aConn = (HttpURLConnection) aUrlObj.openConnection ();
      aConn.setDoOutput (bOutput);
      aConn.setDoInput (bInput);
      aConn.setUseCaches (bUseCaches);
      aConn.setRequestMethod (sRequestMethod);
      aConn.setConnectTimeout (getParameterInt (PARAM_CONNECT_TIMEOUT, 60000));
      aConn.setReadTimeout (getParameterInt (PARAM_READ_TIMEOUT, 60000));
      return aConn;
    }
    catch (final IOException ex)
    {
      throw new WrappedException (ex);
    }
  }

  // Copy headers from an Http connection to an InternetHeaders object
  protected void copyHttpHeaders (@Nonnull final HttpURLConnection aConn, @Nonnull final InternetHeaders aHeaders)
  {
    for (final Map.Entry <String, List <String>> aConnHeader : aConn.getHeaderFields ().entrySet ())
    {
      final String sHeaderName = aConnHeader.getKey ();
      if (sHeaderName != null)
      {
        for (final String sValue : aConnHeader.getValue ())
        {
          if (aHeaders.getHeader (sHeaderName) == null)
            aHeaders.setHeader (sHeaderName, sValue);
          else
            aHeaders.addHeader (sHeaderName, sValue);
        }
      }
    }
  }
}