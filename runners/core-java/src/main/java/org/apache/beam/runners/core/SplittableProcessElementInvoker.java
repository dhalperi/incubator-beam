/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.core;

import javax.annotation.Nullable;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.reflect.DoFnInvoker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.util.WindowedValue;
import org.joda.time.Instant;

/**
 * A runner-specific hook for invoking a {@link DoFn.ProcessElement} method for a splittable {@link
 * DoFn}, in particular, allowing the runner to access the {@link RestrictionTracker}.
 */
public abstract class SplittableProcessElementInvoker<
    InputT, OutputT, RestrictionT, TrackerT extends RestrictionTracker<RestrictionT>> {
  /** Specifies how to resume a splittable {@link DoFn.ProcessElement} call. */
  public class Result {
    @Nullable
    private final RestrictionT residualRestriction;
    private final Instant futureOutputWatermark;

    public Result(
        @Nullable RestrictionT residualRestriction, Instant futureOutputWatermark) {
      this.residualRestriction = residualRestriction;
      this.futureOutputWatermark = futureOutputWatermark;
    }

    /** If {@code null}, means the call should not resume. */
    @Nullable
    public RestrictionT getResidualRestriction() {
      return residualRestriction;
    }

    public Instant getFutureOutputWatermark() {
      return futureOutputWatermark;
    }
  }

  /**
   * Invokes the {@link DoFn.ProcessElement} method using the given {@link DoFnInvoker} for the
   * original {@link DoFn}, on the given element and with the given {@link RestrictionTracker}.
   *
   * @return Information on how to resume the call: residual restriction and a
   * future output watermark.
   */
  public abstract Result invokeProcessElement(
      DoFnInvoker<InputT, OutputT> invoker, WindowedValue<InputT> element, TrackerT tracker);
}
