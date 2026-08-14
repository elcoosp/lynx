// Copyright 2026 The Lynx Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#include "platform/embedder/lynx_ui_renderer.h"

#include <chrono>
#include <string_view>

namespace lynx {
namespace embedder {

namespace {

constexpr int32_t kSyntheticMouseDeviceId = 0;

size_t NowMicros() {
  return static_cast<size_t>(
      std::chrono::duration_cast<std::chrono::microseconds>(
          std::chrono::steady_clock::now().time_since_epoch())
          .count());
}

int64_t ButtonMask(std::string_view button) {
  if (button == "left") {
    return kClayPointerMouseButtonsMousePrimary;
  }
  if (button == "right") {
    return kClayPointerMouseButtonsMouseSecondary;
  }
  if (button == "middle") {
    return kClayPointerMouseButtonsMouseMiddle;
  }
  if (button == "back") {
    return kClayPointerMouseButtonsMouseBack;
  }
  if (button == "forward") {
    return kClayPointerMouseButtonsMouseForward;
  }
  return 0;
}

}  // namespace

void LynxUIRenderer::DispatchSyntheticPointerEvent(
    const char* event_type, float x, float y, const char* button, float delta_x,
    float delta_y, int modifiers, int click_count) {
  (void)modifiers;
  (void)click_count;
  if (!event_type) {
    return;
  }

  const std::string_view type(event_type);
  if (type != "mousePressed" && type != "mouseMoved" &&
      type != "mouseReleased" && type != "mouseWheel") {
    return;
  }

  ClayPointerEvent event = {};
  event.struct_size = sizeof(event);
  event.timestamp = NowMicros();
  event.x = x * pixel_ratio_;
  event.y = y * pixel_ratio_;
  event.device = kSyntheticMouseDeviceId;
  event.device_kind = kClayPointerDeviceKindMouse;

  const int64_t button_mask = ButtonMask(button ? button : "");
  if (type == "mousePressed") {
    event.phase = kClayPointerPhaseDown;
    event.buttons =
        button_mask != 0 ? button_mask : kClayPointerMouseButtonsMousePrimary;
  } else if (type == "mouseReleased") {
    event.phase = kClayPointerPhaseUp;
    event.buttons = 0;
  } else if (type == "mouseMoved") {
    event.phase =
        button_mask != 0 ? kClayPointerPhaseMove : kClayPointerPhaseHover;
    event.buttons = button_mask;
  } else {
    event.phase = kClayPointerPhaseHover;
    event.buttons = 0;
  }

  if (type == "mouseWheel") {
    event.signal_kind = kClayPointerSignalKindScroll;
    event.scroll_delta_x = delta_x * pixel_ratio_;
    event.scroll_delta_y = delta_y * pixel_ratio_;
    event.is_precise_scroll = 1;
  }
  SendPointerEvent(event);
}

}  // namespace embedder
}  // namespace lynx
