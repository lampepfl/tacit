MODEL=deepseek/deepseek-v3.2
DOMAIN=airline
uv run tau2 run \
  --agent llm_agent \
  --domain $DOMAIN \
  --agent-llm openrouter/$MODEL \
  --user-llm openrouter/$MODEL \
  --num-trials 10 --save-to deepseek-v3.2_"$DOMAIN" --max-concurrency 10
