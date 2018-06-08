package com.axonivy.ivy.process.element.blockchain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.HashMap;

import org.junit.Test;

import com.axonivy.ivy.process.element.blockchain.ui.BlockchainRequestUiModel.EthereumModel;

import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;

public class TestEtherumModel
{

  @Test
  public void test()
  {
    EthereumModel model = new EthereumModel();
    model.properties = new HashMap<>();
    model.properties.put("key", "value");
    UserConfig config = EthereumModel.store(model);
    assertThat(config.getRawValue()).isNotEmpty();

    EthereumModel loaded = EthereumModel.load(config);
    assertThat(loaded.properties).containsOnly(entry("key", "value"));
  }
}
