// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.compute.aggregation;

import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.BooleanBlock;
import org.elasticsearch.compute.data.BooleanVector;
import org.elasticsearch.compute.data.DoubleBlock;
import org.elasticsearch.compute.data.DoubleVector;
import org.elasticsearch.compute.data.ElementType;
import org.elasticsearch.compute.data.IntVector;
import org.elasticsearch.compute.data.LongBlock;
import org.elasticsearch.compute.data.LongVector;
import org.elasticsearch.compute.data.Page;

/**
 * {@link GroupingAggregatorFunction} implementation for {@link SumDoubleAggregator}.
 * This class is generated. Do not edit it.
 */
public final class SumDoubleGroupingAggregatorFunction implements GroupingAggregatorFunction {
  private static final List<IntermediateStateDesc> INTERMEDIATE_STATE_DESC = List.of(
      new IntermediateStateDesc("value", ElementType.DOUBLE),
      new IntermediateStateDesc("delta", ElementType.DOUBLE),
      new IntermediateStateDesc("seen", ElementType.BOOLEAN)  );

  private final SumDoubleAggregator.GroupingSumState state;

  private final List<Integer> channels;

  private final BigArrays bigArrays;

  public SumDoubleGroupingAggregatorFunction(List<Integer> channels,
      SumDoubleAggregator.GroupingSumState state, BigArrays bigArrays) {
    this.channels = channels;
    this.state = state;
    this.bigArrays = bigArrays;
  }

  public static SumDoubleGroupingAggregatorFunction create(List<Integer> channels,
      BigArrays bigArrays) {
    return new SumDoubleGroupingAggregatorFunction(channels, SumDoubleAggregator.initGrouping(bigArrays), bigArrays);
  }

  public static List<IntermediateStateDesc> intermediateStateDesc() {
    return INTERMEDIATE_STATE_DESC;
  }

  @Override
  public int intermediateBlockCount() {
    return INTERMEDIATE_STATE_DESC.size();
  }

  @Override
  public GroupingAggregatorFunction.AddInput prepareProcessPage(SeenGroupIds seenGroupIds,
      Page page) {
    Block uncastValuesBlock = page.getBlock(channels.get(0));
    if (uncastValuesBlock.areAllValuesNull()) {
      state.enableGroupIdTracking(seenGroupIds);
      return new GroupingAggregatorFunction.AddInput() {
        @Override
        public void add(int positionOffset, LongBlock groupIds) {
        }

        @Override
        public void add(int positionOffset, LongVector groupIds) {
        }
      };
    }
    DoubleBlock valuesBlock = (DoubleBlock) uncastValuesBlock;
    DoubleVector valuesVector = valuesBlock.asVector();
    if (valuesVector == null) {
      if (valuesBlock.mayHaveNulls()) {
        state.enableGroupIdTracking(seenGroupIds);
      }
      return new GroupingAggregatorFunction.AddInput() {
        @Override
        public void add(int positionOffset, LongBlock groupIds) {
          addRawInput(positionOffset, groupIds, valuesBlock);
        }

        @Override
        public void add(int positionOffset, LongVector groupIds) {
          addRawInput(positionOffset, groupIds, valuesBlock);
        }
      };
    }
    return new GroupingAggregatorFunction.AddInput() {
      @Override
      public void add(int positionOffset, LongBlock groupIds) {
        addRawInput(positionOffset, groupIds, valuesVector);
      }

      @Override
      public void add(int positionOffset, LongVector groupIds) {
        addRawInput(positionOffset, groupIds, valuesVector);
      }
    };
  }

  private void addRawInput(int positionOffset, LongVector groups, DoubleBlock values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      if (values.isNull(groupPosition + positionOffset)) {
        continue;
      }
      int valuesStart = values.getFirstValueIndex(groupPosition + positionOffset);
      int valuesEnd = valuesStart + values.getValueCount(groupPosition + positionOffset);
      for (int v = valuesStart; v < valuesEnd; v++) {
        SumDoubleAggregator.combine(state, groupId, values.getDouble(v));
      }
    }
  }

  private void addRawInput(int positionOffset, LongVector groups, DoubleVector values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      SumDoubleAggregator.combine(state, groupId, values.getDouble(groupPosition + positionOffset));
    }
  }

  private void addRawInput(int positionOffset, LongBlock groups, DoubleBlock values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      if (groups.isNull(groupPosition)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(groupPosition);
      int groupEnd = groupStart + groups.getValueCount(groupPosition);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        if (values.isNull(groupPosition + positionOffset)) {
          continue;
        }
        int valuesStart = values.getFirstValueIndex(groupPosition + positionOffset);
        int valuesEnd = valuesStart + values.getValueCount(groupPosition + positionOffset);
        for (int v = valuesStart; v < valuesEnd; v++) {
          SumDoubleAggregator.combine(state, groupId, values.getDouble(v));
        }
      }
    }
  }

  private void addRawInput(int positionOffset, LongBlock groups, DoubleVector values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      if (groups.isNull(groupPosition)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(groupPosition);
      int groupEnd = groupStart + groups.getValueCount(groupPosition);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        SumDoubleAggregator.combine(state, groupId, values.getDouble(groupPosition + positionOffset));
      }
    }
  }

  @Override
  public void addIntermediateInput(int positionOffset, LongVector groups, Page page) {
    state.enableGroupIdTracking(new SeenGroupIds.Empty());
    assert channels.size() == intermediateBlockCount();
    DoubleVector value = page.<DoubleBlock>getBlock(channels.get(0)).asVector();
    DoubleVector delta = page.<DoubleBlock>getBlock(channels.get(1)).asVector();
    BooleanVector seen = page.<BooleanBlock>getBlock(channels.get(2)).asVector();
    assert value.getPositionCount() == delta.getPositionCount() && value.getPositionCount() == seen.getPositionCount();
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      SumDoubleAggregator.combineIntermediate(state, groupId, value.getDouble(groupPosition + positionOffset), delta.getDouble(groupPosition + positionOffset), seen.getBoolean(groupPosition + positionOffset));
    }
  }

  @Override
  public void addIntermediateRowInput(int groupId, GroupingAggregatorFunction input, int position) {
    if (input.getClass() != getClass()) {
      throw new IllegalArgumentException("expected " + getClass() + "; got " + input.getClass());
    }
    SumDoubleAggregator.GroupingSumState inState = ((SumDoubleGroupingAggregatorFunction) input).state;
    state.enableGroupIdTracking(new SeenGroupIds.Empty());
    SumDoubleAggregator.combineStates(state, groupId, inState, position);
  }

  @Override
  public void evaluateIntermediate(Block[] blocks, int offset, IntVector selected) {
    state.toIntermediate(blocks, offset, selected);
  }

  @Override
  public void evaluateFinal(Block[] blocks, int offset, IntVector selected) {
    blocks[offset] = SumDoubleAggregator.evaluateFinal(state, selected);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("[");
    sb.append("channels=").append(channels);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public void close() {
    state.close();
  }
}
