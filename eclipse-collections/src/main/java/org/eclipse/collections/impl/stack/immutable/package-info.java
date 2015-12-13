/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/**
 * This package contains implementations of the {@link org.eclipse.collections.api.stack.ImmutableStack} interface.
 * <p>
 *     ImmutableStack is the immutable equivalent of MutableStack.
 * <p>
 *     This package contains 3 stack implementations:
 * <ul>
 *     <li>
 *          {@link org.eclipse.collections.impl.stack.immutable.ImmutableArrayStack} - an ImmutableStack backed by a FastList.
 *     </li>
 * </ul>
 * <p>
 *     This package contains one factory implementation:
 * <ul>
 *     <li>
 *         {@link org.eclipse.collections.impl.stack.immutable.ImmutableStackFactoryImpl} - a factory which creates instances of type {@link org.eclipse.collections.api.stack.ImmutableStack}.
 *     </li>
 * </ul>
 */
package org.eclipse.collections.impl.stack.immutable;
