/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-2014 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.executor;

import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.*;

import java.util.concurrent.Executors;

import static org.exist.executor.Module.*;
import static org.exist.xquery.Cardinality.EXACTLY_ONE;
import static org.exist.xquery.value.BooleanValue.FALSE;
import static org.exist.xquery.value.BooleanValue.TRUE;
import static org.exist.xquery.value.Type.INTEGER;
import static org.exist.xquery.value.Type.STRING;

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 *
 */
public class Exec extends BasicFunction {

    public static final String SINGLE_THREAD_EXECUTOR  = "create-single-thread-executor";
    public static final String FIXED_THREAD_POOL  = "create-fixed-thread-pool";
    public static final String CACHED_THREAD_POOL  = "create-cached-thread-pool";
    public static final String SINGLE_THREAD_SCHEDULED_EXECUTOR  = "create-single-thread-scheduled-executor";
    public static final String SCHEDULED_THREAD_POOL  = "create-scheduled-thread-pool";

    public final static FunctionSignature signatures[] = new FunctionSignature[]{
            new FunctionSignature(
                    new QName(SINGLE_THREAD_EXECUTOR, NAMESPACE_URI, PREFIX),
                    "Create single thread executor",
                    new SequenceType[]{
                            new FunctionParameterSequenceType("name", STRING, EXACTLY_ONE, ""),
                    },
                    new FunctionReturnSequenceType(Type.BOOLEAN, EXACTLY_ONE, "Returns true() if executor has been created false() otherwise")
            ),
            new FunctionSignature(
                    new QName(FIXED_THREAD_POOL, NAMESPACE_URI, PREFIX),
                    "Create fixed thread pool",
                    new SequenceType[]{
                            new FunctionParameterSequenceType("name", STRING, EXACTLY_ONE, ""),
                            new FunctionParameterSequenceType("size", INTEGER, EXACTLY_ONE, ""),
                    },
                    new FunctionReturnSequenceType(Type.BOOLEAN, EXACTLY_ONE, "Returns true() if pool has been created false() otherwise")
            ),
            new FunctionSignature(
                    new QName(CACHED_THREAD_POOL, NAMESPACE_URI, PREFIX),
                    "Create cached thread pool",
                    new SequenceType[]{
                            new FunctionParameterSequenceType("name", STRING, EXACTLY_ONE, ""),
                    },
                    new FunctionReturnSequenceType(Type.BOOLEAN, EXACTLY_ONE, "Returns true() if pool has been created false() otherwise")
            ),
            new FunctionSignature(
                    new QName(SINGLE_THREAD_SCHEDULED_EXECUTOR, NAMESPACE_URI, PREFIX),
                    "Create scheduled single thread executor",
                    new SequenceType[]{
                            new FunctionParameterSequenceType("name", STRING, EXACTLY_ONE, ""),
                    },
                    new FunctionReturnSequenceType(Type.BOOLEAN, EXACTLY_ONE, "Returns true() if executor has been created false() otherwise")
            ),
            new FunctionSignature(
                    new QName(SCHEDULED_THREAD_POOL, NAMESPACE_URI, PREFIX),
                    "Create scheduled thread pool",
                    new SequenceType[]{
                            new FunctionParameterSequenceType("name", STRING, EXACTLY_ONE, ""),
                            new FunctionParameterSequenceType("size", INTEGER, EXACTLY_ONE, ""),
                    },
                    new FunctionReturnSequenceType(Type.BOOLEAN, EXACTLY_ONE, "Returns true() if pool has been created false() otherwise")
            ),
    };

    public Exec(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }
    
    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

        String name = args[0].itemAt(0).getStringValue();
        if (executors.get(name) != null) return FALSE;

        if (isCalledAs(SINGLE_THREAD_EXECUTOR))
            executors.put(name, Executors.newSingleThreadExecutor());
        else if (isCalledAs(FIXED_THREAD_POOL))
            executors.put(name, Executors.newFixedThreadPool(((IntegerValue)args[1].itemAt(0)).getInt()));
        else if (isCalledAs(CACHED_THREAD_POOL))
            executors.put(name, Executors.newCachedThreadPool());
        else if (isCalledAs(SINGLE_THREAD_SCHEDULED_EXECUTOR))
            executors.put(name, Executors.newSingleThreadScheduledExecutor());
        else
            executors.put(name, Executors.newScheduledThreadPool(((IntegerValue) args[1].itemAt(0)).getInt()));

        return TRUE;
    }

}
