MODEL=deepseek/deepseek-v3.2
DOMAIN=airline
uv run tau2 run \
  --scala-mode \
  --agent llm_agent \
  --domain $DOMAIN \
  --agent-llm openrouter/$MODEL \
  --user-llm openrouter/$MODEL \
  --num-trials 10 --save-to deepseek-v3.2_"$DOMAIN"_scala --max-concurrency 10
