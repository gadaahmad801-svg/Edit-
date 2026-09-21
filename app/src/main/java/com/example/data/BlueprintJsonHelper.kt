package com.example.data

import com.example.domain.model.*
import org.json.JSONArray
import org.json.JSONObject

object BlueprintJsonHelper {

    fun blueprintToJson(blueprint: EditBlueprint): String {
        val root = JSONObject()
        root.put("id", blueprint.id)
        root.put("version", blueprint.version)
        root.put("title", blueprint.title)
        root.put("createdAt", blueprint.createdAt)

        // Metadata
        val meta = JSONObject()
        meta.put("durationMs", blueprint.referenceMetadata.durationMs)
        meta.put("width", blueprint.referenceMetadata.width)
        meta.put("height", blueprint.referenceMetadata.height)
        meta.put("fps", blueprint.referenceMetadata.fps)
        meta.put("hasAudio", blueprint.referenceMetadata.hasAudio)
        root.put("referenceMetadata", meta)

        // Scenes
        val scenesArr = JSONArray()
        blueprint.scenes.forEach { s ->
            val o = JSONObject()
            o.put("sceneIndex", s.sceneIndex)
            o.put("startMs", s.startMs)
            o.put("endMs", s.endMs)
            o.put("cutType", s.cutType.name)
            o.put("cutConfidence", s.cutConfidence)
            o.put("framingType", s.framingType.name)
            o.put("dominantMotionDirection", s.dominantMotionDirection)
            o.put("brightness", s.brightness)
            o.put("colorProfileId", s.colorProfileId)
            scenesArr.put(o)
        }
        root.put("scenes", scenesArr)

        // Motion Events
        val motionArr = JSONArray()
        blueprint.motionEvents.forEach { m ->
            val o = JSONObject()
            o.put("id", m.id)
            o.put("timestampMs", m.timestampMs)
            o.put("durationMs", m.durationMs)
            o.put("type", m.type.name)
            o.put("intensity", m.intensity)
            o.put("startValue", m.startValue)
            o.put("endValue", m.endValue)
            o.put("curve", m.curve.name)
            o.put("normalizedTargetX", m.normalizedTargetX)
            o.put("normalizedTargetY", m.normalizedTargetY)
            o.put("confidence", m.confidence)
            motionArr.put(o)
        }
        root.put("motionEvents", motionArr)

        // Keyframes
        val kfArr = JSONArray()
        blueprint.keyframes.forEach { k ->
            val o = JSONObject()
            o.put("timestampMs", k.timestampMs)
            o.put("scale", k.scale)
            o.put("translationX", k.translationX)
            o.put("translationY", k.translationY)
            o.put("rotationDeg", k.rotationDeg)
            o.put("opacity", k.opacity)
            o.put("curve", k.curve.name)
            kfArr.put(o)
        }
        root.put("keyframes", kfArr)

        // Speed Events
        val spArr = JSONArray()
        blueprint.speedEvents.forEach { sp ->
            val o = JSONObject()
            o.put("startMs", sp.startMs)
            o.put("endMs", sp.endMs)
            o.put("speedMultiplier", sp.speedMultiplier)
            o.put("type", sp.type.name)
            o.put("curve", sp.curve.name)
            spArr.put(o)
        }
        root.put("speedEvents", spArr)

        // Transitions
        val trArr = JSONArray()
        blueprint.transitions.forEach { tr ->
            val o = JSONObject()
            o.put("atTimestampMs", tr.atTimestampMs)
            o.put("durationMs", tr.durationMs)
            o.put("type", tr.type.name)
            o.put("intensity", tr.intensity)
            o.put("approximationAvailable", tr.approximationAvailable)
            o.put("confidence", tr.confidence)
            trArr.put(o)
        }
        root.put("transitions", trArr)

        // Color Events
        val clrArr = JSONArray()
        blueprint.colorEvents.forEach { c ->
            val o = JSONObject()
            o.put("id", c.id)
            o.put("name", c.name)
            o.put("exposure", c.exposure)
            o.put("contrast", c.contrast)
            o.put("saturation", c.saturation)
            o.put("temperature", c.temperature)
            o.put("tint", c.tint)
            o.put("highlights", c.highlights)
            o.put("shadows", c.shadows)
            o.put("vignette", c.vignette)
            o.put("grain", c.grain)
            o.put("hueShiftDeg", c.hueShiftDeg)
            clrArr.put(o)
        }
        root.put("colorEvents", clrArr)

        // Text Events
        val txtArr = JSONArray()
        blueprint.textEvents.forEach { t ->
            val o = JSONObject()
            o.put("id", t.id)
            o.put("text", t.text)
            o.put("startMs", t.startMs)
            o.put("endMs", t.endMs)
            o.put("normalizedX", t.normalizedX)
            o.put("normalizedY", t.normalizedY)
            o.put("scale", t.scale)
            o.put("rotationDeg", t.rotationDeg)
            o.put("colorHex", t.colorHex)
            o.put("animationType", t.animationType)
            o.put("isEditable", t.isEditable)
            txtArr.put(o)
        }
        root.put("textEvents", txtArr)

        // Overlay Events
        val ovArr = JSONArray()
        blueprint.overlayEvents.forEach { ov ->
            val o = JSONObject()
            o.put("id", ov.id)
            o.put("label", ov.label)
            o.put("startMs", ov.startMs)
            o.put("endMs", ov.endMs)
            o.put("normalizedX", ov.normalizedX)
            o.put("normalizedY", ov.normalizedY)
            o.put("scale", ov.scale)
            o.put("opacity", ov.opacity)
            o.put("blendMode", ov.blendMode)
            o.put("extractionQualityPercent", ov.extractionQualityPercent)
            ovArr.put(o)
        }
        root.put("overlayEvents", ovArr)

        // Audio Map
        val audioObj = JSONObject()
        audioObj.put("bpm", blueprint.audioMap.bpm)
        audioObj.put("isAudioPresent", blueprint.audioMap.isAudioPresent)
        val beatsArr = JSONArray()
        blueprint.audioMap.beats.forEach { b ->
            val bo = JSONObject()
            bo.put("timestampMs", b.timestampMs)
            bo.put("energy", b.energy)
            bo.put("isMajorDrop", b.isMajorDrop)
            bo.put("isKick", b.isKick)
            beatsArr.put(bo)
        }
        audioObj.put("beats", beatsArr)
        root.put("audioMap", audioObj)

        // Style Profile
        val styleObj = JSONObject()
        styleObj.put("pace", blueprint.overallStyle.pace)
        styleObj.put("motionIntensity", blueprint.overallStyle.motionIntensity)
        styleObj.put("zoomIntensity", blueprint.overallStyle.zoomIntensity)
        styleObj.put("transitionAggression", blueprint.overallStyle.transitionAggression)
        styleObj.put("colorStyle", blueprint.overallStyle.colorStyle)
        styleObj.put("beatSyncStrength", blueprint.overallStyle.beatSyncStrength)
        root.put("overallStyle", styleObj)

        // Confidences
        val confObj = JSONObject()
        blueprint.confidences.forEach { (k, v) -> confObj.put(k, v) }
        root.put("confidences", confObj)

        // Unsupported Effects
        val unArr = JSONArray()
        blueprint.unsupportedEffects.forEach { u ->
            val uo = JSONObject()
            uo.put("effectName", u.effectName)
            uo.put("reason", u.reason)
            uo.put("approximationAvailable", u.approximationAvailable)
            uo.put("userActionChosen", u.userActionChosen)
            unArr.put(uo)
        }
        root.put("unsupportedEffects", unArr)

        return root.toString()
    }

    fun jsonToBlueprint(jsonStr: String): EditBlueprint {
        val root = JSONObject(jsonStr)
        val id = root.optString("id", "bp_${System.currentTimeMillis()}")
        val version = root.optString("version", "1.0")
        val title = root.optString("title", "Edit Blueprint")
        val createdAt = root.optLong("createdAt", System.currentTimeMillis())

        val metaObj = root.optJSONObject("referenceMetadata") ?: JSONObject()
        val meta = MediaMetadata(
            durationMs = metaObj.optLong("durationMs", 15000L),
            width = metaObj.optInt("width", 1920),
            height = metaObj.optInt("height", 1080),
            fps = metaObj.optInt("fps", 30),
            hasAudio = metaObj.optBoolean("hasAudio", true)
        )

        val scenes = mutableListOf<SceneBoundary>()
        root.optJSONArray("scenes")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                scenes.add(
                    SceneBoundary(
                        sceneIndex = o.optInt("sceneIndex", i),
                        startMs = o.optLong("startMs", 0L),
                        endMs = o.optLong("endMs", 2000L),
                        cutType = try { CutType.valueOf(o.optString("cutType", "HARD_CUT")) } catch (_: Exception) { CutType.HARD_CUT },
                        cutConfidence = o.optDouble("cutConfidence", 0.9).toFloat(),
                        framingType = try { FramingType.valueOf(o.optString("framingType", "MEDIUM")) } catch (_: Exception) { FramingType.MEDIUM },
                        dominantMotionDirection = o.optString("dominantMotionDirection", "Static"),
                        brightness = o.optDouble("brightness", 0.5).toFloat(),
                        colorProfileId = o.optString("colorProfileId", "default")
                    )
                )
            }
        }

        val motionEvents = mutableListOf<MotionEvent>()
        root.optJSONArray("motionEvents")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                motionEvents.add(
                    MotionEvent(
                        id = o.optString("id", "m_$i"),
                        timestampMs = o.optLong("timestampMs", 0L),
                        durationMs = o.optLong("durationMs", 600L),
                        type = try { MotionType.valueOf(o.optString("type", "ZOOM_IN")) } catch (_: Exception) { MotionType.ZOOM_IN },
                        intensity = o.optDouble("intensity", 0.5).toFloat(),
                        startValue = o.optDouble("startValue", 1.0).toFloat(),
                        endValue = o.optDouble("endValue", 1.2).toFloat(),
                        curve = try { InterpolationCurve.valueOf(o.optString("curve", "SMOOTH")) } catch (_: Exception) { InterpolationCurve.SMOOTH },
                        normalizedTargetX = o.optDouble("normalizedTargetX", 0.5).toFloat(),
                        normalizedTargetY = o.optDouble("normalizedTargetY", 0.5).toFloat(),
                        confidence = o.optDouble("confidence", 0.9).toFloat()
                    )
                )
            }
        }

        val keyframes = mutableListOf<TransformKeyframe>()
        root.optJSONArray("keyframes")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                keyframes.add(
                    TransformKeyframe(
                        timestampMs = o.optLong("timestampMs", 0L),
                        scale = o.optDouble("scale", 1.0).toFloat(),
                        translationX = o.optDouble("translationX", 0.0).toFloat(),
                        translationY = o.optDouble("translationY", 0.0).toFloat(),
                        rotationDeg = o.optDouble("rotationDeg", 0.0).toFloat(),
                        opacity = o.optDouble("opacity", 1.0).toFloat(),
                        curve = try { InterpolationCurve.valueOf(o.optString("curve", "SMOOTH")) } catch (_: Exception) { InterpolationCurve.SMOOTH }
                    )
                )
            }
        }

        val speedEvents = mutableListOf<SpeedEvent>()
        root.optJSONArray("speedEvents")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                speedEvents.add(
                    SpeedEvent(
                        startMs = o.optLong("startMs", 0L),
                        endMs = o.optLong("endMs", 1000L),
                        speedMultiplier = o.optDouble("speedMultiplier", 1.0).toFloat(),
                        type = try { SpeedType.valueOf(o.optString("type", "NORMAL")) } catch (_: Exception) { SpeedType.NORMAL },
                        curve = try { InterpolationCurve.valueOf(o.optString("curve", "SMOOTH")) } catch (_: Exception) { InterpolationCurve.SMOOTH }
                    )
                )
            }
        }

        val transitions = mutableListOf<TransitionEvent>()
        root.optJSONArray("transitions")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                transitions.add(
                    TransitionEvent(
                        atTimestampMs = o.optLong("atTimestampMs", 0L),
                        durationMs = o.optLong("durationMs", 400L),
                        type = try { TransitionType.valueOf(o.optString("type", "CUT")) } catch (_: Exception) { TransitionType.CUT },
                        intensity = o.optDouble("intensity", 0.8).toFloat(),
                        approximationAvailable = o.optBoolean("approximationAvailable", true),
                        confidence = o.optDouble("confidence", 0.88).toFloat()
                    )
                )
            }
        }

        val colorEvents = mutableListOf<ColorProfile>()
        root.optJSONArray("colorEvents")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                colorEvents.add(
                    ColorProfile(
                        id = o.optString("id", "clr_$i"),
                        name = o.optString("name", "Grade Look"),
                        exposure = o.optDouble("exposure", 0.0).toFloat(),
                        contrast = o.optDouble("contrast", 0.15).toFloat(),
                        saturation = o.optDouble("saturation", 0.20).toFloat(),
                        temperature = o.optDouble("temperature", 0.05).toFloat(),
                        tint = o.optDouble("tint", 0.0).toFloat(),
                        highlights = o.optDouble("highlights", -0.1).toFloat(),
                        shadows = o.optDouble("shadows", 0.08).toFloat(),
                        vignette = o.optDouble("vignette", 0.25).toFloat(),
                        grain = o.optDouble("grain", 0.06).toFloat(),
                        hueShiftDeg = o.optDouble("hueShiftDeg", 0.0).toFloat()
                    )
                )
            }
        }

        val textEvents = mutableListOf<TextEvent>()
        root.optJSONArray("textEvents")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                textEvents.add(
                    TextEvent(
                        id = o.optString("id", "txt_$i"),
                        text = o.optString("text", ""),
                        startMs = o.optLong("startMs", 0L),
                        endMs = o.optLong("endMs", 2000L),
                        normalizedX = o.optDouble("normalizedX", 0.5).toFloat(),
                        normalizedY = o.optDouble("normalizedY", 0.8).toFloat(),
                        scale = o.optDouble("scale", 1.0).toFloat(),
                        rotationDeg = o.optDouble("rotationDeg", 0.0).toFloat(),
                        colorHex = o.optString("colorHex", "#FFFFFF"),
                        animationType = o.optString("animationType", "Fade In"),
                        isEditable = o.optBoolean("isEditable", true)
                    )
                )
            }
        }

        val overlayEvents = mutableListOf<OverlayEvent>()
        root.optJSONArray("overlayEvents")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                overlayEvents.add(
                    OverlayEvent(
                        id = o.optString("id", "ov_$i"),
                        label = o.optString("label", "Overlay"),
                        startMs = o.optLong("startMs", 0L),
                        endMs = o.optLong("endMs", 2000L),
                        normalizedX = o.optDouble("normalizedX", 0.5).toFloat(),
                        normalizedY = o.optDouble("normalizedY", 0.5).toFloat(),
                        scale = o.optDouble("scale", 1.0).toFloat(),
                        opacity = o.optDouble("opacity", 0.8).toFloat(),
                        blendMode = o.optString("blendMode", "Screen"),
                        extractionQualityPercent = o.optInt("extractionQualityPercent", 85)
                    )
                )
            }
        }

        val audioMapObj = root.optJSONObject("audioMap") ?: JSONObject()
        val beats = mutableListOf<AudioBeat>()
        audioMapObj.optJSONArray("beats")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                beats.add(
                    AudioBeat(
                        timestampMs = o.optLong("timestampMs", 0L),
                        energy = o.optDouble("energy", 0.8).toFloat(),
                        isMajorDrop = o.optBoolean("isMajorDrop", false),
                        isKick = o.optBoolean("isKick", true)
                    )
                )
            }
        }
        val audioMap = AudioMap(
            bpm = audioMapObj.optInt("bpm", 120),
            beats = beats,
            isAudioPresent = audioMapObj.optBoolean("isAudioPresent", true)
        )

        val styleObj = root.optJSONObject("overallStyle") ?: JSONObject()
        val overallStyle = EditStyleProfile(
            pace = styleObj.optString("pace", "Fast Dynamic"),
            motionIntensity = styleObj.optString("motionIntensity", "High Motion"),
            zoomIntensity = styleObj.optString("zoomIntensity", "Medium Punch"),
            transitionAggression = styleObj.optString("transitionAggression", "Aggressive"),
            colorStyle = styleObj.optString("colorStyle", "High Contrast Cinematic"),
            beatSyncStrength = styleObj.optString("beatSyncStrength", "Strong Locked")
        )

        val confMap = mutableMapOf<String, Float>()
        root.optJSONObject("confidences")?.let { confObj ->
            val keys = confObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                confMap[k] = confObj.optDouble(k, 0.9).toFloat()
            }
        }

        val unsupported = mutableListOf<UnsupportedEffectInfo>()
        root.optJSONArray("unsupportedEffects")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                unsupported.add(
                    UnsupportedEffectInfo(
                        effectName = o.optString("effectName", "Complex Lens Flare"),
                        reason = o.optString("reason", "Proprietary pixel fusion"),
                        approximationAvailable = o.optBoolean("approximationAvailable", true),
                        userActionChosen = o.optString("userActionChosen", "Approximate")
                    )
                )
            }
        }

        return EditBlueprint(
            id = id,
            version = version,
            title = title,
            createdAt = createdAt,
            referenceMetadata = meta,
            scenes = scenes,
            motionEvents = motionEvents,
            keyframes = keyframes,
            speedEvents = speedEvents,
            transitions = transitions,
            colorEvents = colorEvents,
            textEvents = textEvents,
            overlayEvents = overlayEvents,
            audioMap = audioMap,
            overallStyle = overallStyle,
            confidences = confMap,
            unsupportedEffects = unsupported
        )
    }

    fun transferConfigToJson(config: TransferConfig): String {
        val o = JSONObject()
        o.put("transferTiming", config.transferTiming)
        o.put("transferCuts", config.transferCuts)
        o.put("transferMotion", config.transferMotion)
        o.put("transferZoom", config.transferZoom)
        o.put("transferRotation", config.transferRotation)
        o.put("transferSpeed", config.transferSpeed)
        o.put("transferTransitions", config.transferTransitions)
        o.put("transferColor", config.transferColor)
        o.put("transferFilterLook", config.transferFilterLook)
        o.put("transferText", config.transferText)
        o.put("transferOverlays", config.transferOverlays)
        o.put("transferBeatSync", config.transferBeatSync)
        o.put("transferCameraMotion", config.transferCameraMotion)
        o.put("transferSmartCrop", config.transferSmartCrop)
        o.put("matchStrength", config.matchStrength.name)
        o.put("mode", config.mode.name)
        return o.toString()
    }

    fun jsonToTransferConfig(jsonStr: String): TransferConfig {
        return try {
            val o = JSONObject(jsonStr)
            TransferConfig(
                transferTiming = o.optBoolean("transferTiming", true),
                transferCuts = o.optBoolean("transferCuts", true),
                transferMotion = o.optBoolean("transferMotion", true),
                transferZoom = o.optBoolean("transferZoom", true),
                transferRotation = o.optBoolean("transferRotation", true),
                transferSpeed = o.optBoolean("transferSpeed", true),
                transferTransitions = o.optBoolean("transferTransitions", true),
                transferColor = o.optBoolean("transferColor", true),
                transferFilterLook = o.optBoolean("transferFilterLook", true),
                transferText = o.optBoolean("transferText", false),
                transferOverlays = o.optBoolean("transferOverlays", false),
                transferBeatSync = o.optBoolean("transferBeatSync", true),
                transferCameraMotion = o.optBoolean("transferCameraMotion", true),
                transferSmartCrop = o.optBoolean("transferSmartCrop", true),
                matchStrength = try { MatchStrength.valueOf(o.optString("matchStrength", "BALANCED")) } catch (_: Exception) { MatchStrength.BALANCED },
                mode = try { TransferMode.valueOf(o.optString("mode", "FULL_EDIT")) } catch (_: Exception) { TransferMode.FULL_EDIT }
            )
        } catch (_: Exception) {
            TransferConfig()
        }
    }

    fun stringListToJson(list: List<String>): String {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        return arr.toString()
    }

    fun jsonToStringList(jsonStr: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }
}
