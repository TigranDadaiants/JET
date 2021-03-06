/*
 * Copyright 2018 Tigran Dadaiants
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tddts.evetrader.view.fx.dialog;

import com.github.tddts.sprix.annotations.Message;
import com.github.tddts.sprix.annotations.SprixDialog;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.PostConstruct;

/**
 * Dialog to input developer credentials.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@SprixDialog(value = "fxml/dialog-devc.fxml", cached = true)
public class DevCredentialsDialog extends Dialog<Pair<String, String>> {

  @FXML
  private TextField clientIdField;
  @FXML
  private PasswordField secretKeyField;

  @Message("msg.cancel")
  private String cancelMessage;

  public DevCredentialsDialog() {
  }

  @PostConstruct
  public void init() {
    ButtonType cancelButtonType = new ButtonType(cancelMessage, ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().addAll(ButtonType.OK, cancelButtonType);
    setResultConverter(this::getResult);
  }

  private Pair<String, String> getResult(ButtonType buttonType) {
    if (buttonType == ButtonType.OK) {
      return Pair.of(clientIdField.getText(), secretKeyField.getText());
    }
    return null;
  }
}
